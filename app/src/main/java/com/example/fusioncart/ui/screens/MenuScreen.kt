package com.example.fusioncart.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fusioncart.FusionCartApplication
import com.example.fusioncart.model.MenuItem
import com.example.fusioncart.viewmodel.CartViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    restaurantId: String,
    restaurantName: String,
    onBackClick: () -> Unit,
    cartViewModel: CartViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var menuItems by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(restaurantName) {
        Log.d("MenuScreen", "Loading menu items for restaurant: $restaurantName (ID: $restaurantId)")
        // Load menu items
        val menuRef = FusionCartApplication.database.reference.child("restaurants").child(restaurantId).child("menu")
        Log.d("MenuScreen", "Database reference path: ${menuRef}")
        
        menuRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("MenuScreen", "Data snapshot exists: ${snapshot.exists()}")
                    Log.d("MenuScreen", "Number of children: ${snapshot.childrenCount}")
                    
                    val items = mutableListOf<MenuItem>()
                    snapshot.children.forEach { child ->
                        Log.d("MenuScreen", "Processing menu item with key: ${child.key}")
                        if (child.key != "0") { // Skip the first null entry
                            val name = child.child("name").getValue(String::class.java)
                            val price = child.child("price").getValue(String::class.java)
                            val type = child.child("type").getValue(String::class.java)
                            val image = child.child("image").getValue(String::class.java)
                            
                            Log.d("MenuScreen", "Menu item data - Name: $name, Price: $price, Type: $type, Image: $image")
                            
                            if (!name.isNullOrEmpty() && !price.isNullOrEmpty()) {
                                items.add(MenuItem(name, price, image, type))
                                Log.d("MenuScreen", "Added menu item: $name")
                            } else {
                                Log.d("MenuScreen", "Skipped menu item due to missing name or price")
                            }
                        }
                    }
                    Log.d("MenuScreen", "Total menu items loaded: ${items.size}")
                    menuItems = items
                    isLoading = false
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Log.e("MenuScreen", "Error loading menu: ${error.message}")
                    isLoading = false
                }
            })
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar with restaurant name and back button
        SmallTopAppBar(
            title = { Text(text = restaurantName) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search menu items") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        // Menu items list
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filteredItems = if (searchQuery.isEmpty()) {
                    menuItems
                } else {
                    menuItems.filter { item ->
                        item.name.contains(searchQuery, ignoreCase = true) ||
                        item.type?.contains(searchQuery, ignoreCase = true) == true
                    }
                }

                items(filteredItems) { menuItem ->
                    MenuItemCard(
                        menuItem = menuItem,
                        restaurantName = restaurantName,
                        cartViewModel = cartViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    restaurantName: String,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier
) {
    val cart by cartViewModel.cart.collectAsState()
    val itemQuantity = cart.items["${menuItem.name}_$restaurantName"]?.quantity ?: 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (menuItem.image != null) {
                AsyncImage(
                    model = menuItem.image,
                    contentDescription = menuItem.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                menuItem.type?.let { type ->
                    Text(
                        text = type,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    text = menuItem.price,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFE23744),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (itemQuantity > 0) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE23744))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { cartViewModel.removeItem(menuItem, restaurantName) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = Color.White
                            )
                        }
                        
                        Text(
                            text = itemQuantity.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        
                        IconButton(
                            onClick = { cartViewModel.addItem(menuItem, restaurantName) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { cartViewModel.addItem(menuItem, restaurantName) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE23744)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text("ADD", color = Color.White)
                    }
                }
            }
        }
    }
}
