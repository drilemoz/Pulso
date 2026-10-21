package br.com.pulsobateria.labs

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.security.KeyStore
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Credenciais de sessões anteriores: criptografadas com chave não exportável AndroidKeyStore.
 * Não são incluídas no backup do app; a autorização só vale enquanto o host a mantiver.
 */
internal class RecentRemoteDevices(private val context: Context) {
    private val prefs = context.getSharedPreferences("remote_labs_trusted_v1", Context.MODE_PRIVATE)
    private val alias = "br.com.pulsobateria.remote_recent_aes_v1"

    data class Entry(
        val id: String,
        val host: String,
        val port: Int,
        val name: String,
        val clientId: String,
        val secret: String,
        val lastUsed: Long,
    )

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build())
        return generator.generateKey()
    }

    private fun encode(entries: List<Entry>): String {
        val array = JSONArray()
        entries.take(5).forEach { entry ->
            array.put(JSONObject().put("id",entry.id).put("host",entry.host).put("port",entry.port)
                .put("name",entry.name).put("clientId",entry.clientId)
                .put("secret",entry.secret).put("lastUsed",entry.lastUsed))
        }
        val cipher=Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val bytes=cipher.doFinal(array.toString().toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cipher.iv+bytes, Base64.NO_WRAP)
    }

    @Synchronized fun entries(): List<Entry> = runCatching {
        val encoded=prefs.getString("recent",null) ?: return emptyList()
        val bytes=Base64.decode(encoded, Base64.NO_WRAP)
        require(bytes.size > 12+16)
        val cipher=Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE,key(),GCMParameterSpec(128,bytes.copyOfRange(0,12)))
        val array=JSONArray(String(cipher.doFinal(bytes.copyOfRange(12,bytes.size)),Charsets.UTF_8))
        buildList {
            for (i in 0 until minOf(array.length(),5)) {
                val node=array.getJSONObject(i)
                val host=node.getString("host")
                val port=node.getInt("port")
                val clientId=node.getString("clientId")
                val secret=node.getString("secret")
                if (!isLocalIpv4(host) || port !in 1024..65535 ||
                    !clientId.matches(Regex("[0-9a-f]{32}")) || !secret.matches(Regex("[0-9a-f]{32}"))) continue
                add(Entry(node.getString("id"),host,port,node.getString("name").take(48),
                    clientId,secret,node.optLong("lastUsed",0)))
            }
        }
    }.getOrElse {
        // Keystore invalidado (p. ex. reinstalação/restauração): nunca retornar credenciais corrompidas.
        prefs.edit().remove("recent").apply()
        emptyList()
    }

    @Synchronized fun remember(host: String,port: Int,name: String,clientId: String,secret: String): Entry? = runCatching {
        require(isLocalIpv4(host) && port in 1024..65535)
        val entry=Entry(UUID.randomUUID().toString(),host,port,name.take(48),clientId,secret,System.currentTimeMillis())
        val updated=listOf(entry) + entries().filterNot { it.host==host && it.name==name }
        check(prefs.edit().putString("recent",encode(updated.take(5))).commit())
        entry
    }.getOrNull()

    @Synchronized fun touch(id: String) {
        val current=entries();val match=current.firstOrNull { it.id==id } ?: return
        runCatching { prefs.edit().putString("recent",encode(listOf(match.copy(lastUsed=System.currentTimeMillis()))+current.filterNot { it.id==id })).apply() }
    }

    @Synchronized fun forget(id: String) {
        runCatching { prefs.edit().putString("recent",encode(entries().filterNot { it.id==id })).apply() }
    }
}

data class RecentDevice(val id: String, val name: String, val address: String, val lastUsed: Long)
