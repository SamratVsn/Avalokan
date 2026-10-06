package com.example.avalokan.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.avalokan.R
import com.example.avalokan.ui.theme.AvalokanTheme

@Composable
fun ProfileScreen(){
    Column(){
        Spacer(modifier = Modifier.padding(16.dp))
        UserSection()
        Spacer(modifier = Modifier.padding(32.dp))
        HeritageCollection()
        Spacer(modifier = Modifier.padding(16.dp))
        UpcomingRegistrations()
    }
}

@Composable
private fun UserSection(){
    Column(){
//        Photo
        Text(
            text = "Samrat Parajuli"
        ) //Username
        Text(
            text = "Exploring heritage of Nepal"
        ) //Short Bio
        Row(){
            UserSectionInfo(12, "VISITED")
            UserSectionInfo(45, "SAVED")
            UserSectionInfo(3, "EVENTS")
        }
    }
}

@Composable
private fun UserSectionInfo(
    number: Int,
    text: String,
){
    Column(){
        Text(
            text = "$number"
        )
        Text(
            text = text
        )
    }
}

@Composable
private fun HeritageCollection(){
    Column(){
        Row(){
            Text(
                text = stringResource(R.string.myHeritageColl)
            )
            Text(
                text = stringResource(R.string.manage)
            )
        }
        CollectionCards("Durbar Square", "Kathmandu")
    }
}

@Composable
private fun CollectionCards(
    place: String,
    loc: String
){
    Column(){
        //Photo
        Text(
            text = place
        )
        Text(
            text = loc
        )
    }
}

@Composable
private fun UpcomingRegistrations(){
    Column(){
        Text(text = stringResource(R.string.upcomingReg))
        RegisteredBox()
    }
}

@Composable
private fun RegisteredBox(){
    Row(){
        Column(){
            Text("SEPT")
            Text("17")
        }
        Column(){
            Text("Indra Jatra")
            Row(){
                Text("Durbar Square")
                Text(".")
                Text("12.00 PM")
            }
        }
    }
}

@Preview
@Composable
private fun ProfilePreview(){
    AvalokanTheme() {
        ProfileScreen()
    }
}