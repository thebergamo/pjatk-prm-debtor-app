package br.com.thedon.debtorapp.ui.debtor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.thedon.debtorapp.DebtorTopAppBar
import br.com.thedon.debtorapp.R
import br.com.thedon.debtorapp.ui.AppViewModelProvider
import br.com.thedon.debtorapp.ui.navigation.NavigationDestination
import br.com.thedon.debtorapp.ui.theme.DebtorAppTheme
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale

object DebtorEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.debtor_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtorEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    viewModel: DebtorEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            DebtorTopAppBar(
                title = stringResource(DebtorEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Note: If the user rotates the screen very fast, the operation may get cancelled
                    // and the item may not be saved in the Database. This is because when config
                    // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                    // be cancelled - since the scope is bound to composition.
                    coroutineScope.launch {
                        viewModel.saveDebtor()
                        navigateBack()
                    }
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.save_action)
                )

            }
        }
    ) { innerPadding ->
        DebtorEntryBody(
            itemUiState = viewModel.debtorUiState,
            onItemValueChange = viewModel::updateUiState,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun DebtorEntryBody(
    itemUiState: DebtorUiState,
    onItemValueChange: (DebtorDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        DebtorInputForm(
            debtorDetails = itemUiState.debtorDetails,
            onValueChange = onItemValueChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtorInputForm(
    debtorDetails: DebtorDetails,
    modifier: Modifier = Modifier,
    onValueChange: (DebtorDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = debtorDetails.name,
            onValueChange = { onValueChange(debtorDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.debtor_name_req)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = debtorDetails.debt,
            onValueChange = { onValueChange(debtorDetails.copy(debt = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(R.string.debtor_debt_req)) },
            leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DebtorEntryScreenPreview() {
    DebtorAppTheme {
        DebtorEntryBody(
            itemUiState = DebtorUiState(
                DebtorDetails(
                    name = "Debtor name",
                    debt = "10.00",
                )
            ),
            onItemValueChange = {},
        )
    }
}