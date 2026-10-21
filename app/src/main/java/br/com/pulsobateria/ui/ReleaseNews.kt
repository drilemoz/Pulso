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
 const val VERSION_NAME = "0.1.0-alpha.71"
 const val VERSION_CODE = 71
 val ITEMS = listOf(
  "Monitoramento remoto abre sem pop-up obrigatória: ações Ser monitorado e Monitorar outro celular ficam visíveis no topo do Labs.",
  "Aparelhos recentes permite reconectar sem novo QR enquanto a autorização do monitorado continuar válida; credenciais locais são protegidas pelo AndroidKeyStore.",
  "Carga automática inicia com um toque, sem prazo nem seleção de Solar/Elétrico, preservando a origem informada pelo Android sem atribuir a fonte incorretamente.",
  "Carga automática finaliza ao atingir 100% ou após três minutos consecutivos de desconexão confirmada, mantendo relatório e lacunas observacionais.",
  "Painel remoto destaca percentual, ganho na bateria e previsão, recolhendo temperaturas e outros detalhes técnicos sem ocultar os horários do teste.",
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
