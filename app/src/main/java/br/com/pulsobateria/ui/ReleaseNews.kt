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
 const val VERSION_NAME = "0.1.0-alpha.72"
 const val VERSION_CODE = 72
 val ITEMS = listOf(
  "Solar, Elétrico e Consumo começam sem selecionar duração; encerramento e duração efetiva continuam registrados no relatório.",
  "A estimativa de término aos 100% ganha cartão grande no Labs local e destaque junto à bateria no painel remoto.",
  "Sem progresso suficiente, a tela informa que aguarda medições; o Consumo mantém encerramento manual sem previsão inventada.",
  "O formulário remoto deixa de pedir tempo e envia testes solares/elétricos sem prazo, preservando a finalização opcional aos 100%.",
  "Testes com prazo de versões anteriores mantêm suas datas originais no histórico, sem alterar Room v11 ou backup JSON v9.",
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
