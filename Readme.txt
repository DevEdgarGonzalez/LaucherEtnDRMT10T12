
# LauncherGEN2018_T12T10

Esta aplicacion es un reprodutor de contenido multimedia el cual se localiza en el master (Memoria SD) el cual debe de tener la estructura especifica para el proyecto.


## Comenzando

Estas instrucciones te permitirán obtener una copia del proyecto en funcionamiento en tu máquina local para propósitos de desarrollo y pruebas.

### Compilacion:
    - Se requiere colocar el proyecto "exoPlayerLib" como libreria a nivel proyecto
    - requiere en librerias a nivel app RootTools2.6.jar y wasabi.jar
    - para bluethoot requiere en src/aidl/acom.android.com.android.bluetooth el archivo IBluetoothHeadset.aidl
    - los proyectos en C en src/main/jniLibs/armeabi libcom_jni.so y libWasabiJni.so

### prerequisitos

Android studio

### Librerias Jar
-RootTools2.6.jar
-wasabi.jar

### JniLibs
_libcom_jni.so
_libWasabiJni.so



## Especificaciones
### Update 2020-11-18 version: 1.8

|Name|Enable|
| ------ | ------ |
|categorias dinamicas (config.json)                                 |Si|
|categorias ninos dinamicas(config.json)                            |Si|
|min categorias (pantalla principal)                                |1|
|max categorias (pantalla principal)                                |6|
|brillo                                                             |Si|
| ------ | ------ |
|ordenamiento alfabetico                                            |Si|
|MenuMantenimiento                                                  |Si|
|MenuTest                                                           |Si|
| ------ | ------ |
|ServiceActia                                                       |Si|
|ServiceMovies                                                      |Si|
| ------ | ------ |
|iSeat                                                              |Si|
|Actracking                                                         |No|
| ------ | ------ |
|AutoTokens                                                         |Si|
|DRM                                                                |Si|
|Subcategorias Movies                                               |Si|
|Subcategorias abooks                                               |Si|
|Multilenguaje                                                      |NO|
|Texto justificado                                                  |Si|
|Bluetooth                                                          |Si|
| ------ | ------ |
|Nosotros: imagen, varias imagenes, carpetas de imagenes, video     |-|
|ayuda: imagen, varias imagenes, carpetas de imagenes, video        |-|



## json.conf
### Update 2020-11-18 version: 1.8

las actividades a abrir son:
|Modulo|Activity|
| ------ | ------ |
|juegos             |com.actia.games.GamesActivity|
|Audilibros         |com.actia.audiolibros.AbookGenreActivity|
|Musica             |com.actia.music.MusicGenresActivity|
|Movies             |com.actia.peliculas.PeliculasActivity|
|Ninos              |com.actia.ninos.NinosActivity|
|Nosotros           |com.actia.nosotros.NosotrosMainActivity|
|MusicNinos         |com.actia.music_ninos.MusicNinosActivity|
|AudiolibrosNinos   |com.actia.abook_ninos.AbookNinosActivity|
|MoviesNinos        |com.actia.movies_ninos.MoviesNinosActivity|
|MusicaNinos        |com.actia.music_ninos.MusicNinosActivity|


