package br.com.thedon.debtorapp.ui.debtor

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.thedon.debtorapp.DebtorTopAppBar
import br.com.thedon.debtorapp.R
import br.com.thedon.debtorapp.ui.AppViewModelProvider
import br.com.thedon.debtorapp.ui.components.ConfirmDialog
import br.com.thedon.debtorapp.ui.navigation.NavigationDestination
import br.com.thedon.debtorapp.ui.theme.DebtorAppTheme
import kotlinx.coroutines.launch
import java.text.NumberFormat

object DebtorEditDestination : NavigationDestination {
    override val route = "item_edit"
    override val titleRes = R.string.edit_item_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtorEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToSimulation: (DebtorDetails) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DebtorEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            DebtorTopAppBar(
                title = stringResource(DebtorEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        val debtor = viewModel.debtorUiState.debtorDetails.toDebtor()
                        val debtValue = NumberFormat.getCurrencyInstance().format(debtor.debt)
                        val message = "Hey ${debtor.name} kindly reminder about debt of ${debtValue} you owe me o/"
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, message)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    },
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.share_debit)
                    )

                }
                SmallFloatingActionButton(
                    onClick = {
                        navigateToSimulation(viewModel.debtorUiState.debtorDetails)
                    },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_insights),
                        contentDescription = stringResource(R.string.simulate)
                    )

                }
                FloatingActionButton(
                    onClick = {
                        if (viewModel.isDebtorModified()) {
                            viewModel.showChangesDialog()
                        } else {
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
        }
    ) { innerPadding ->
        ConfirmDialog(
            show = viewModel.confirmDialog,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = stringResource(R.string.dialog_save_icon_description),
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                )

            },
            content = stringResource(R.string.dialog_save_warning_content),
            dissmissActionText = stringResource(R.string.dialog_save_action_no),
            confirmActionText = stringResource(R.string.dialog_save_action_yes),
            onDismiss = viewModel::hideChangesDialog,
            onConfirm = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be updated in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.updateDebtor()
                    navigateBack()
                }
            }
        )
        DebtorEntryBody(
            itemUiState = viewModel.debtorUiState,
            onItemValueChange = viewModel::updateUiState,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DebtorEditRoutePreview() {
    DebtorAppTheme {
        DebtorEditScreen(navigateBack = { /*Do nothing*/ }, onNavigateUp = { /*Do nothing*/ }, navigateToSimulation = {/* Do nothing */})
    }
}