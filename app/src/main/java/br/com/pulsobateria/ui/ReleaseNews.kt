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
 const val VERSION_NAME = "0.1.0-alpha.69"
 const val VERSION_CODE = 69
 val ITEMS = listOf(
  "Monitoramento remoto agora abre escolha entre ser monitorado e monitorar outro aparelho, com troca de função explícita.",
  "Leitor QR foi integrado à janela vertical do Labs, com permissão de câmera sob demanda e controle da lanterna.",
  "Controle LAN passa a iniciar testes solares e elétricos, com duração, condições e finalização opcional aos 100%.",
  "Aparelho monitorado identifica o nome informado pelo controlador, mostra notificação de acesso e permite revogar pela tela ou notificação.",
  "O novo pareamento usa QR temporário de uso único e sessão separada para reconexão; relatório permanece no aparelho medido.",
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
