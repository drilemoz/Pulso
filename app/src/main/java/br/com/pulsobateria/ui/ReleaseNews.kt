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
 const val VERSION_NAME = "0.1.0-alpha.63"
 const val VERSION_CODE = 63
 val ITEMS = listOf(
  "Labs ganha Monitoramento remoto com geração de QR Code no celular monitorado e leitura no celular controlador.",
  "A conexão funciona pela mesma rede Wi‑Fi local, sem internet, exibindo bateria, corrente, temperatura e tempo do teste remoto.",
  "O controlador pode iniciar e finalizar remotamente o teste solar com configuração rápida de céu, painel e duração.",
  "A interface do Monitoramento remoto adota o layout em dois cartões Celular A/Celular B e o painel compacto de acompanhamento pedido para a beta.",
  "A release adiciona permissões e dependências locais para QR Code, câmera e comunicação LAN do recurso remoto.",

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
