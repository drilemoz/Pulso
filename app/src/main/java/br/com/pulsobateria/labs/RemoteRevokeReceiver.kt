package br.com.pulsobateria.labs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import br.com.pulsobateria.PulsoApp

/** Botão privado da notificação local de acesso. Nunca é exportado. */
class RemoteRevokeReceiver : BroadcastReceiver() {
    companion object { const val REVOKE = "br.com.pulsobateria.action.REVOKE_REMOTE_LABS" }
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != REVOKE) return
        (context.applicationContext as? PulsoApp)?.remoteLabs?.revokeAccess()
    }
}
