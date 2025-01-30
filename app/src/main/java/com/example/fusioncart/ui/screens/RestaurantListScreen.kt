package com.example.fusioncart.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fusioncart.FusionCartApplication
import com.example.fusioncart.model.Restaurant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@Composable
fun RestaurantListScreen(
    modifier: Modifier = Modifier,
    onRestaurantClick: (String) -> Unit = {}
) {
    var restaurants by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            val restaurantsRef = FusionCartApplication.database.getReference("Restaurants")
            Log.d("RestaurantListScreen", "Getting reference to Restaurants")
            
            restaurantsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("RestaurantListScreen", "Data snapshot received: ${snapshot.childrenCount} items")
                    
                    val restaurantList = mutableListOf<Restaurant>()
                    snapshot.children.forEach { child ->
                        val restaurant = child.getValue(Restaurant::class.java)
                        if (restaurant != null) {
                            restaurantList.add(restaurant)
                        }
                    }
                    restaurants = restaurantList
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("RestaurantListScreen", "Error loading restaurants: ${error.message}")
                    Toast.makeText(context, "Error loading restaurants", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            })
        } catch (e: Exception) {
            Log.e("RestaurantListScreen", "Error: ${e.message}")
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(restaurants) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    onRestaurantClick = { onRestaurantClick(restaurant.name) }
                )
            }
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onRestaurantClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f)
            )
            .clickable(onClick = onRestaurantClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color.Black.copy(alpha = 0.3f)
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = restaurant.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Surface(
                    modifier = Modifier.padding(vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = restaurant.tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewRestaurantList() {
    RestaurantListScreen()
}
