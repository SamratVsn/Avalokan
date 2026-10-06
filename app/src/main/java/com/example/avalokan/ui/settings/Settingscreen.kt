package com.example.avalokan.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.avalokan.R

@Composable
fun SettingsScreen(){
    Column(){
        Text(text = stringResource(R.string.settings))
//        Spacer
        AccountSetting()
//        Spacer
        SuppNFeed()
//        Spacer
        SettingCard(stringResource(R.string.logIn))
    }
}

@Composable
private fun AccountSetting(){
    Column() {
        Text(
            text = stringResource(R.string.account)
        )
        SettingCard(stringResource(R.string.editProfile))
        SettingCard(stringResource(R.string.security))
        SettingCard(stringResource(R.string.notifications))
    }
}

@Composable
private fun SuppNFeed(){
    Column(){
        Text(
            text = stringResource(R.string.suppNFe)
        )
        SettingCard(stringResource(R.string.sendSugRep))
        SettingCard(stringResource(R.string.appVersion))
    }
}

@Composable
private fun SettingCard(
//    @DrawableRes icon: Int,
    settingName: String
){
    Row(){
        //    Icon() put icon here
        Text(text = settingName)
    }
}

