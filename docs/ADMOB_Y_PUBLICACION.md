# LauraApps — Chat Fácil: publicación y monetización

## AdMob antes de producción

1. En AdMob crea una app **Android** llamada `Chat Fácil: Sin Guardar Número` con estado **No publicada**.
2. Crea una unidad **Banner**, sin anuncios invasivos al abrir chats.
3. Conserva los dos identificadores:
   - ID de aplicación: `ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY`.
   - ID del banner: `ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ`.
4. Configura mensajes de consentimiento UMP donde se requieran, y verifica el formulario de privacidad.
5. Los APK `debug` SIEMPRE usan anuncios de prueba oficiales de Google, sin importar propiedades configuradas. Para `release`, Gradle exige `-PenableRealAds=true -PadMobAppId=... -PadMobBannerId=...` y falla sin identificadores reales.
6. Una vez publicada en Google Play y accesible públicamente, vincula su ficha desde AdMob. La revisión completa y la autorización de app-ads.txt pueden depender de que esté publicada y vinculada.
7. Verifica que en la ficha de Google Play el sitio web sea `https://tommymsc.github.io/`, y que el archivo `https://tommymsc.github.io/app-ads.txt` incluya el editor correcto.
8. Revisa en Play Console y AdMob las declaraciones de uso de datos del SDK Mobile Ads, UMP e ID de publicidad antes de publicar.

## Política de privacidad y controles

`https://tommymsc.github.io/chat-facil-privacy.html`

- No solicita permisos de contactos, SMS ni llamadas.
- El usuario confirma manualmente el envío desde la app externa.
- Los números, favoritos e historial residen localmente; si el usuario abre el chat, el destinatario del enlace recibe el número y posible texto preparado.
- No incluir logos ni marcas que sugieran afiliación con WhatsApp/Meta.
- Evitar automatización masiva o spam.

## Google Play

**Nombre ES:** Chat Fácil: Sin Guardar Número.

**Nombre EN:** Easy Chat: No Save Contact.

**Package release:** `com.lauraapps.chatfacil`.

**Requisitos de entrega**:
- AAB de producción **firmado** con clave de carga segura.
- Capturas reales en teléfonos, ficha en español `es-419` e inglés `en-US`.
- Target API 36, clasificación, contenido, público objetivo, seguridad de datos, declaración de anuncios e ID publicitario.
- Prueba cerrada y verificadores exigidos a la cuenta.

No subir un APK DEBUG a Google Play. No afirmar que la cuenta/aplicación esté aprobada antes de la revisión efectiva.
