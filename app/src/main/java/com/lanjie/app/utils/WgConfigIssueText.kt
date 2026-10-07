package com.lanjie.app.utils

import android.content.res.Resources
import com.lanjie.app.R

fun WgConfigIssue.describe(res: Resources): String = when (this) {
    WgConfigIssue.NoAllowedIps -> res.getString(R.string.wireguard_issue_no_allowed_ips)
    is WgConfigIssue.MalformedAllowedIps ->
        res.getString(R.string.wireguard_issue_malformed_allowed_ips, entries.joinToString(", "))
}
