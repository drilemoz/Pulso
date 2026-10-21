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
 const val VERSION_NAME = "0.1.0-alpha.73"
 const val VERSION_CODE = 73
 val ITEMS = listOf(
  "O painel remoto apresenta cartões de corrente e temperatura com a mesma altura e unidades na mesma linha.",
  "O ganho de bateria no controle remoto passa a ser exibido em %, com indicação de Carga automática quando aplicável, sem usar p.p. na interface.",
  "Uma nova notificação no celular controlador avisa quando a carga monitorada chega a 100%, perde a fonte ou termina, sem repetir alertas por atualização.",
  "A notificação de conclusão remota solicita permissão no controlador após o pareamento e abre o Labs; o relatório continua salvo no aparelho monitorado.",
  "A auditoria confirma que uma nova leitura real de 100% é persistida antes de encerrar o teste solar, elétrico ou automático; sem alterações no Room ou backup.",
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
