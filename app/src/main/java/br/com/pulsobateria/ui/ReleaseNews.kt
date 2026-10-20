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
 const val VERSION_NAME = "0.1.0-alpha.67"
 const val VERSION_CODE = 67
 val ITEMS = listOf(
  "Labs Solar e Carregamento Elétrico recebem Finalizar ao atingir 100%, ligado por padrão e disponível mesmo na duração manual.",
  "Uma nova leitura válida de 100% encerra automaticamente o teste, preserva os valores observados e emite a notificação de conclusão.",
  "O histórico e o relatório distinguem término por bateria cheia, por prazo e por ação manual, sem desligar fisicamente o carregador.",
  "Room v11 e backup JSON v9 preservam a escolha individual do teste, com migração que não ativa a função em registros antigos.",

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
