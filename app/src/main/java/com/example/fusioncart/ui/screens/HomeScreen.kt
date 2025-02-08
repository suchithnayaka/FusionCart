package com.example.fusioncart.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fusioncart.model.Restaurant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.fusioncart.FusionCartApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRestaurantClick: (Restaurant) -> Unit,
    modifier: Modifier = Modifier
) {
    var restaurants by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    var address by remember { mutableStateOf<String?>(null) }
    var isVegOnly by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Load restaurants
    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "Loading restaurants")
        FusionCartApplication.database.reference.child("restaurants")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("HomeScreen", "Data snapshot exists: ${snapshot.exists()}")
                    Log.d("HomeScreen", "Number of restaurants: ${snapshot.childrenCount}")
                    
                    val restaurantsList = mutableListOf<Restaurant>()
                    snapshot.children.forEach { child ->
                        val id = child.key ?: return@forEach
                        val name = child.child("name").getValue(String::class.java) ?: ""
                        val image = child.child("image").getValue(String::class.java) ?: ""
                        val location = child.child("location").getValue(String::class.java) ?: ""
                        val rating = child.child("rating").getValue(Double::class.java) ?: 0.0
                        val cuisine = child.child("cuisine").getValue(String::class.java) ?: ""
                        val deliveryTime = child.child("deliveryTime").getValue(Int::class.java) ?: 30
                        val costForTwo = child.child("costForTwo").getValue(Int::class.java) ?: 0
                        val isVeg = child.child("isVeg").getValue(Boolean::class.java) ?: false
                        val tag = child.child("tag").getValue(String::class.java) ?: ""
                        
                        Log.d("HomeScreen", "Restaurant data - ID: $id, Name: $name")
                        
                        if (name.isNotEmpty()) {
                            restaurantsList.add(
                                Restaurant(
                                    id = id,
                                    name = name,
                                    image = image,
                                    location = location,
                                    rating = rating,
                                    cuisine = cuisine,
                                    deliveryTime = deliveryTime,
                                    costForTwo = costForTwo,
                                    isVeg = isVeg,
                                    tag = tag
                                )
                            )
                            Log.d("HomeScreen", "Added restaurant: $name")
                        }
                    }
                    Log.d("HomeScreen", "Total restaurants loaded: ${restaurantsList.size}")
                    restaurants = restaurantsList
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeScreen", "Error loading restaurants: ${error.message}")
                }
            })
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Address Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE23744)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Address Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Home",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = address ?: "Add Address",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    // Veg Only Toggle
                    Switch(
                        checked = isVegOnly,
                        onCheckedChange = { isVegOnly = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Green,
                            checkedTrackColor = Color.White
                        )
                    )
                    Text(
                        text = "VEG",
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    placeholder = { Text("Search \"biryani\"...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("Sort", "Great Offers", "Rating 4.0+", "Pure Veg")) { filter ->
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = { Text(filter) }
                        )
                    }
                }
            }

            // Restaurant Count
            item {
                Text(
                    text = "${restaurants.size} restaurants delivering to you",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Restaurants List
            items(restaurants) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    onRestaurantClick = { onRestaurantClick(restaurant) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    // Load restaurants from Firebase
    LaunchedEffect(Unit) {
        val restaurantsRef = FusionCartApplication.database.getReference("Restaurants")
        restaurantsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restaurantsList = mutableListOf<Restaurant>()
                snapshot.children.forEach { child ->
                    child.getValue(Restaurant::class.java)?.let { restaurant ->
                        restaurantsList.add(restaurant.copy(id = child.key ?: ""))
                    }
                }
                restaurants = restaurantsList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onRestaurantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onRestaurantClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = restaurant.image,
                contentDescription = restaurant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${restaurant.rating}★",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = restaurant.cuisine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = restaurant.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
