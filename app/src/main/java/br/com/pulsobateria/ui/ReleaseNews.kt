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
 const val VERSION_NAME = "0.1.0-alpha.65"
 const val VERSION_CODE = 65
 val ITEMS = listOf(
  "Labs recebe o Teste de Carregamento Elétrico com duração manual ou programada e verificação da fonte conectada informada pelo Android.",
  "O carregamento elétrico registra bateria inicial e final, tempo efetivo carregando, corrente líquida média e lacunas observacionais.",
  "Temperaturas inicial, média aritmética de leituras válidas, final e máxima ficam no relatório e histórico do Labs.",
  "Room v10 migra a tabela Labs preservando resultados anteriores sem inventar médias térmicas retroativas.",
  "Backup JSON v8 preserva a média térmica e a origem elétrica, mantendo leitura de backups antigos.",

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
