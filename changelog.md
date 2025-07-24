# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

##[v1.8.9] - 2021-02-23
### Added
- Se abren settings bletooth en lugar de manipular los dispositivos en el launcher
### Removed
- Se elimina lista de dispositivos com.android.bluetooth


 
[v1.8.8] - 2021-02-23
### Added
- diseño visual para tactil 7

### Deleted
- Se elimina animacion en pantalla principal en tactil 7
- Se elimina animacion en header de todas las categorias



## [v1.8.7] - 2020-12-14
### Fixed
- Se agrega random a abooks

## [v1.8.6] - 2020-12-14
### Added
- Iconos y Nombres cortados en el Genero de Musica
- Boton ATRÁS mas grande para en Genero Audio Libros de Niños.


## [v1.8.5]
### Added
- Nombre de categorias de ninos se maneja a traves de config.json

### Fixed
- Al abrir una nueva instacia de la aplicacion se detiene los audiolibros (seccion general y ninos)


## [v1.8.4]
### Changed
- cambiar nombre a STP100
- Agregar a T a tokens

### Fixed
- children movies back no regresa a ninos activity
- children games back no regresa a ninos activity+
- Realizar el aleatorio sobre el contenido del genero
- audiolibros: no se detiene reproduccion al reproducir el botn home
- Se corrige bug al navegar en las categorias de ninos
- Se corrige bug al navegar en las categorias de ninos




## [v1.8.3] - 2020-12-01
### Fixed
- Se cambian iconos de barra inferior
- Se arregla bug de no mostrar random
- Se oculta nombre de categorias de peliculas

## [v1.8.2] - 2020-12-01

### Fixed
- Se corrige navegacion entre categorias
- Se elimina navegacion entre submenus y categorias
- se habilita click en la portada de abook ninos


## [v1.8.1] - 2020-11-25
### Fixed
- Se corrige navegacion em Subgeneros de audiolibros
- Se corrige navegacion en subgenero de peliculas

## [v1.8] - 2020-11-18
- Rename ContextoGeneric_t10_t12 a ContextoApp
- Rename ConstantesGeneric_t10_t12 a ConstantsApp
- Los categorias de peliculas ahora pueden tener subcategorias
- La visualizacion de categorias acepta hasta 6
- si un subgenero de movies (youtubers) o podcast no tiene contenido, entonces no se visualiza
- Se agrega apartado de submenus con maximo dos elementos en pantalla principal
- Se actualiza readme
- Podcast y audiolibros pueden ser: 1 abook, varios abooks, 1 directorio o N directorios
- Se agrega dinamico la opcion de "packageName" abre un subgenero (aplica en abooks y movies)
- en Abooks si solo existe un elemento se abre la reproduccion, si hay 2 o mas elementos entonces muestra categorias
- En abooks genres Se agrega botones de scroll
- se cambia TwoWaveView por Recycler en MainActivity
- Random lee ahora las peliculas en subcategorias


## [1.7] - 2020-08-17 ( v1.7.1)
### Added
- Se crea web view fragment el cual se controla con el archivo web.json
- Se habilita la encuesta al terminar video publicitario
- Se crea clase que administra la llamada a encuesta

##[1.6.2] - 2020-08-12
### Added
-Se crea clase que determina el contenido a mostrar en aboutUs y Help con el fin de homologar funcionalidad
### Fixed
-Se oculta boton random en toda la app

##[1.6.1] - 2020-08-12
### Added
-Se crea fragment que determina mostrar una imagen o slider, esto se manda a llamar en help y about us
### Removed
-Se elimina fragment que solo muestra una imagen, se mostraba en help y ayuda

## [1.5.1] - 2020-07-13
### Fixed
- se acomoda en bottombar los iconos de atras y adelante
- se ajusta diseño visual en tactil 12 cuando se tienen 4 actegorias

## [1.5] - 2020-07-09

### Fixed
- Video publicitario ordenarlo alfabeticamente
- quitar funcionalidad de la botonera y dejarlo en la pantalla
- cambiar nombrea LauncherGEN2018_T12T10v1.X
- En pantalla Error Activity corregir bug de apertura de xilon
- Se quita funcionalidad de menu de botonera en pantalla ErrorActivity
- en detalle pelicula quitar focus
- recorrer boton de ayuda niños
- recorrer el boton de ayuda a la derecha

### Removed
- Quitar opcion de tokens
- boton de niños quitarlo

### Added
- se cambia icono de launcher
- peliculas niños: quitar boton niños
- juegos niños: quitar boton niños
- brillo al iniciar tactil al 100re

- niños: boton atras
- niños: boton brillo


## [1.4] - 2020-07-06
### Fixed
- Se modifica diseño visual para que solo se vean 3 categorias en la pantalla principal
- se inahabilita iseat
- Se agrega validacion cuando no existen peliculas de niños
- Se agrega validacion dependiendo de las categorias que se tenagn de ninos
- Se corrige bug de waveAdapter cuando se hace scroll en la pantalla principal

## [1.2.3] 3-07-2019
### Fixed
-se corrige bug de error al reproducir peliculas menores de 10 seg por tiempo en banners

## [1.2.2] 19-06-2019
###Fixed
-Se corrige bug en Video promocional; la causa era que se intentaba ocultar la barra superior dos veces.

## [1.2.1] 23-05-2019
### Added
-Se agrega funcion de brillo en la mayoria de las pantasllas
-Se agrega opcion de conexion via com.android.bluetooth
-Se agrega navegacion con control remoto 
-Se agrega aleatorio por categorias generales y niños: audiolibros, musica y peliculas
-se agrega modulo iSeat

### Fixed
-Se adecua opcion de nosotros y ayuda, se puede visualizar dependiendo del contenido en la memoria SD (una sola imagen, directorio de imagenes, video o texto)
-Se justifican textos
-Las imagenes de la pantalla principal de niños las toma de la memoria Sd


