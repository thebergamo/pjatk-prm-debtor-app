package br.com.thedon.debtorapp.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.thedon.debtorapp.DebtorTopAppBar
import br.com.thedon.debtorapp.R
import br.com.thedon.debtorapp.data.debtor.Debtor
import br.com.thedon.debtorapp.ui.AppViewModelProvider
import br.com.thedon.debtorapp.ui.components.ConfirmDialog
import br.com.thedon.debtorapp.ui.components.EmptyState
import br.com.thedon.debtorapp.ui.navigation.NavigationDestination
import br.com.thedon.debtorapp.ui.theme.DebtorAppTheme
import java.text.NumberFormat

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    navigateToItemUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DebtorTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.debtor_entry_title)
                )
            }

        },
    ) { innerPadding ->
        ConfirmDialog(
            show = homeUiState.showDeleteDialog,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.dialog_icon_description),
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                )
            },
            title = stringResource(R.string.dialog_delete_debtor_title),
            content = stringResource(R.string.dialog_delete_debtor_description),
            confirmActionText = stringResource(R.string.dialog_delete_action_yes),
            dissmissActionText = stringResource(R.string.dialog_delete_action_no),
            onDismiss = viewModel::hideDeleteDebtor,
            onConfirm = viewModel::deleteDebtor
        )
        HomeBody(
            itemList = homeUiState.itemList,
            totalDebt = homeUiState.totalDebt,
            onItemClick = navigateToItemUpdate,
            onItemLongClick = viewModel::showDeleteDebtor,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun HomeBody(
    itemList: List<Debtor>,
    totalDebt: Double,
    onItemClick: (Int) -> Unit,
    onItemLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var verticalArrangement = Arrangement.Top
    if (itemList.isEmpty()) {
        verticalArrangement = Arrangement.Center
    }
    Column(
        verticalArrangement = verticalArrangement,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.padding_small)),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                modifier = Modifier.size(46.dp),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(id = R.string.logo_description)
            )

        }
        if (itemList.isEmpty()) {
            EmptyState(
                text = stringResource(R.string.no_debtors_description),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = stringResource(
                    R.string.debtor_sum,
                    NumberFormat.getCurrencyInstance().format(totalDebt)
                ),
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.padding_medium),
                        vertical = dimensionResource(id = R.dimen.padding_small)
                    )
            )

            DebtorList(
                itemList = itemList,
                onItemClick = { onItemClick(it.id) },
                onItemLongClick = { onItemLongClick(it.id) },
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_small),
                    vertical = dimensionResource(id = R.dimen.padding_large)
                )
            )
        }
    }
//    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DebtorList(
    itemList: List<Debtor>,
    onItemClick: (Debtor) -> Unit,
    onItemLongClick: (Debtor) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = itemList, key = { it.id }) { item ->
            DebtorItem(
                item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .combinedClickable(
                        onClick = { onItemClick(item) },
                        onLongClick = { onItemLongClick(item) }
                    )
            )
        }
    }
}

@Composable
private fun DebtorItem(
    item: Debtor, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = NumberFormat.getCurrencyInstance().format(item.debt),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    DebtorAppTheme {
        HomeBody(listOf(
            Debtor(1, "John", 100.0), Debtor(2, "Mary", 200.0), Debtor(3, "Beau", 300.0)
        ), totalDebt = 600.0, onItemClick = {}, onItemLongClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    DebtorAppTheme {
        HomeBody(listOf(), totalDebt = 0.0, onItemClick = {}, onItemLongClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DebtorItemPreview() {
    DebtorAppTheme {
        DebtorItem(
            Debtor(1, "John", 100.0),
        )
    }
}