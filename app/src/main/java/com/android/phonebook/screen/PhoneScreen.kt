package com.android.phonebook.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.android.phonebook.domain.model.PhoneModel
import com.android.phonebook.routing.Screen
import com.android.phonebook.ui.components.AppDrawer
import com.android.phonebook.ui.components.Phone
import com.android.phonebook.ui.components.Search
import com.android.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun PhoneScreen(viewModel: MainViewModel) {
    val phones: List<PhoneModel> by viewModel.phonesNotInTrash.observeAsState(listOf())

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val coroutineScope = rememberCoroutineScope()

    val query = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            PhoneTopAppBar(
                onNavigationIconClick = {
                    coroutineScope.launch { scaffoldState.drawerState.open() }
                }
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Phones,
                closeDrawerAction = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreateNewPhoneClick() },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Phone Button"
                    )
                }
            )
        }
    )
    {
        if (phones.isNotEmpty()) {
            Column() {
                Search(searchBy = query)
                PhonesList(
                    phones = search(query.value, phones),
                    onPhoneCheckedChange = { viewModel.onPhoneCheckedChange(it) },
                    onPhoneClick = { viewModel.onPhoneClick(it) }
                )
            }
        }
    }
}

fun search(searchBy: String, phoneList: List<PhoneModel>): List<PhoneModel> {
    val res: ArrayList<PhoneModel> = ArrayList()
    for (phone in phoneList) {
        val searchKey =
            phone.first_name + phone.middle_name + phone.last_name + phone.tag + phone.contact
        if (searchKey.contains(searchBy, ignoreCase = true)) {
            res.add(phone)
        }
    }
    return res.toList()
}

@Composable
fun PhoneTopAppBar(
    onNavigationIconClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "My Phone book",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Drawer Button"
                )
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun PhonesList(
    phones: List<PhoneModel>,
    onPhoneCheckedChange: (PhoneModel) -> Unit,
    onPhoneClick: (PhoneModel) -> Unit
) {
    LazyColumn {
        items(count = phones.size) { phoneIndex ->
            val phone = phones[phoneIndex]
            Phone(
                phone = phone,
                onPhoneClick = onPhoneClick,
                onPhoneCheckedChange = onPhoneCheckedChange,
                isSelected = false
            )
        }
    }
}


@ExperimentalMaterialApi
@Preview
@Composable
private fun PhonesListPreview() {
    PhonesList(
        phones = listOf(
            PhoneModel(1, "A", "", "Ant", "0000000000", "Mobile", null),
            PhoneModel(2, "B", "", "Bird", "1111111111", "Home", false),
            PhoneModel(3, "C", "", "Cat", "2222222222", "Work", true)
        ),
        onPhoneCheckedChange = {},
        onPhoneClick = {}
    )
}