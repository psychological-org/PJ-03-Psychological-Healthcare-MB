package com.example.beaceful.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.beaceful.R
import com.example.beaceful.ui.components.CustomSearchBar
import com.example.beaceful.ui.components.cards.CustomerList
import com.example.beaceful.ui.viewmodel.AppointmentViewModel

@Composable
fun CustomerScreen(
    navController: NavHostController,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val patients = viewModel.getPatients(2)
    Column(modifier = Modifier.fillMaxHeight().padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.cu1),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
        CustomSearchBar(
            suggestions = null,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (patients.isNotEmpty())
            CustomerList(customers = patients, navController = navController)
    }
}

@Preview
@Composable
fun Pre(){
    CustomerScreen(
        navController = rememberNavController()
    )
}