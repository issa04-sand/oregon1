# 📁 Carpeta de Sprites

## 🎨 Coloca tus sprites aquí

Esta carpeta es donde debes colocar todas tus imágenes del juego.

### Estructura Recomendada:

```
sprites/
├── player/
│   ├── player_idle.png          (40x40 px)
│   ├── player_walk_up.png
│   ├── player_walk_down.png
│   ├── player_walk_left.png
│   └── player_walk_right.png
│
├── enemies/
│   ├── enemy_bandit.png         (35x35 px)
│   ├── enemy_wolf.png
│   └── enemy_bear.png
│
├── items/
│   ├── food.png                 (20x20 px)
│   ├── medicine.png
│   └── ammo.png
│
├── ui/
│   ├── heart_full.png           (24x24 px)
│   ├── heart_empty.png
│   └── reticle.png              (40x40 px)
│
└── backgrounds/
    ├── llanuras_bg.png          (900x600 px)
    ├── montanas_bg.png          (900x600 px)
    └── rio_bg.png               (900x600 px)
```

## 🛠️ Cómo Obtener Sprites

### Opción 1: Generador HTML (Incluido)
1. Abre `sprite_generator.html` (en la raíz del proyecto)
2. Dibuja o usa los sprites por defecto
3. Descarga cada sprite
4. Guárdalos en las carpetas correspondientes

### Opción 2: Sitios Gratuitos
- **OpenGameArt.org** - https://opengameart.org/
- **Itch.io** - https://itch.io/game-assets/free
- **Kenney.nl** - https://www.kenney.nl/assets

### Opción 3: Sin Sprites
Si no agregas sprites, el juego funciona automáticamente con figuras geométricas simples (círculos de colores).

## 📝 Notas Importantes

- **Formato**: PNG con transparencia (alpha channel)
- **Nombres**: Deben coincidir exactamente con los especificados
- **Tamaños**: Usar los tamaños recomendados para mejor rendimiento
- **Licencias**: Si usas sprites de terceros, verifica las licencias

## 🔗 Documentación Completa

Lee `docs/GUIA_SPRITES.md` para:
- Guía completa de sprites
- Tutoriales paso a paso
- Solución de problemas
- Recursos adicionales

## ⚙️ Activar el Sistema de Sprites

Para usar sprites en lugar de figuras geométricas:

1. Coloca tus sprites en estas carpetas
2. En `MainMenuView.java` (línea 97), cambia:
   ```java
   GameView gameView = new GameView(stage, controller);
   ```
   Por:
   ```java
   GameViewWithSprites gameView = new GameViewWithSprites(stage, controller);
   ```
3. Recompila: `mvn clean compile`
4. Ejecuta: `mvn javafx:run`

---

**¡El juego funciona perfectamente con o sin sprites!**
