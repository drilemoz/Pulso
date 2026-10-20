package br.com.pulsobateria.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.pulsobateria.BuildConfig

/** Alterações exclusivas do build atual. Atualizar obrigatoriamente em cada release. */
internal object ReleaseNews {
 const val VERSION_NAME = "0.1.0-alpha.64"
 const val VERSION_CODE = 64
 val ITEMS = listOf(
  "Corrigida colisão entre o comando JSON e o método de envio, que impediria compilar a inicialização solar remota.",
  "Corrigido o teste de Novidades para reconhecer a versão anterior sem depender de uma data fixa no CHANGELOG.",
  "O contrato de integração AIDL permanece protegido enquanto a verificação diferencia a comunicação LAN autorizada.",
  "Pareamento remoto agora aceita somente IPv4 privado e o servidor escuta apenas na interface Wi-Fi selecionada.",
  "Conexões LAN têm prazo máximo de tentativa e leituras remotas antigas não são apresentadas como atuais.",
  "A tela remota identifica medições reais, bloqueia comandos quando desconectada e solicita monitoramento no celular A.",

 )
}

@Composable
fun ReleaseNewsDialog(onClose: () -> Unit) {
 AlertDialog(onDismissRequest=onClose,
  title={ Text("Novidades • ${ReleaseNews.VERSION_NAME}") },
  text={ LazyColumn(verticalArrangement=Arrangement.spacedBy(12.dp)) {
   items(ReleaseNews.ITEMS) { item -> Text("• $item") }
  } },
  confirmButton={ TextButton(onClick=onClose) { Text("Entendi") } })
}

/** O indicador é gravado somente após fechar a tela, não ao abri-la. */
internal fun newsAlreadySeen(context: Context): Boolean =
 context.getSharedPreferences("release_news",Context.MODE_PRIVATE)
  .getInt("last_seen_version",-1) == BuildConfig.VERSION_CODE

internal fun markNewsSeen(context: Context) {
 context.getSharedPreferences("release_news",Context.MODE_PRIVATE).edit()
  .putInt("last_seen_version",BuildConfig.VERSION_CODE).apply()
}
