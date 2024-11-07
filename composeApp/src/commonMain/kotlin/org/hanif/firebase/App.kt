package org.hanif.firebase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import multiplatform.network.cmptoast.ToastDuration
import multiplatform.network.cmptoast.showToast
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        // FirebaseAuthentication()
        FirebaseRealtimeDB()
    }
}

@Composable
private fun FirebaseAuthentication() {
    val scope = rememberCoroutineScope()
    val auth = remember { Firebase.auth }
    var firebaseUser: FirebaseUser? by remember { mutableStateOf(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(auth.currentUser) {
        firebaseUser = auth
            .currentUser
    }

    if (firebaseUser == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(25.dp))
            OutlinedButton(onClick = {
                scope.launch {
                    try {
                        auth.createUserWithEmailAndPassword(email, password)
                    } catch (e: Exception) {
                        auth.signInWithEmailAndPassword(email, password)
                    }
                }
            }) {
                Text("Login")
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = firebaseUser!!.email ?: "")
            Spacer(modifier = Modifier.height(25.dp))
            OutlinedButton(onClick = {
                scope.launch {
                    auth.signOut()
                    firebaseUser = auth.currentUser
                }

            }) {
                Text("Login")
            }
        }
    }
}


@Composable
fun FirebaseRealtimeDB() {
    val scope = rememberCoroutineScope()
    val db = remember { Firebase.database.reference() }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val list = remember { mutableStateListOf<User?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            db.valueEvents
                .map { dataSnapshot ->
                    list.clear()
                    for (ds in dataSnapshot.children) {
                        val d = ds.value as HashMap<*, String>
                        val name1 = d["name"]
                        val email1 = d["email"]
                        val password1 = d["password"]
                        val u = User(name1!!, email1!!, password1!!)
                        list.add(u)
                    }

                }.collect()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Name") }
        )
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(25.dp))
        OutlinedButton(onClick = {
            scope.launch {
                try {
                    db.child(name).setValue(User(name, email, password))
                } catch (e: Exception) {
                    showToast(
                        message = e.message ?: "unknown error.",
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        duration = ToastDuration.Short
                    )
                }
            }
        }) {
            Text("Insert")
        }
        Spacer(modifier = Modifier.height(25.dp))
        if (list.size != 0) {
            showToast(
                message = "!@#$ Here1",
                backgroundColor = Color.White,
                textColor = Color.Black,
                duration = ToastDuration.Short
            )
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(list) { index, item ->
                    Card(Modifier.fillMaxWidth().wrapContentHeight()) {
                        item?.name?.let {
                            Text(
                                it,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        } ?: CircularProgressIndicator(color = Color.Black,)
                    }
                }
            }
        } else {
            showToast(
                message = "!@#$ Here",
                backgroundColor = Color.White,
                textColor = Color.Black,
                duration = ToastDuration.Short
            )
            Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                CircularProgressIndicator(color = Color.Black)
            }
        }
    }
}

@Serializable
data class User(
    val name: String = "",
    val email: String = "",
    val password: String = ""
)