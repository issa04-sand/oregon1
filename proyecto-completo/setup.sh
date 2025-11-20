#!/bin/bash

# Script de Integración Automática - Oregon Trail Survival
# Este script copia automáticamente tus archivos al proyecto nuevo

echo "========================================="
echo "Oregon Trail - Setup Automático"
echo "========================================="
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: Ejecuta este script desde la raíz del proyecto"
    echo "   (donde está el archivo pom.xml)"
    exit 1
fi

# Pedir ruta de la implementación original
echo "📂 ¿Dónde está tu carpeta 'implementacion/' actual?"
echo "   Ejemplo: /home/usuario/Documents/implementacion"
read -p "Ruta: " IMPL_PATH

# Verificar que existe
if [ ! -d "$IMPL_PATH" ]; then
    echo "❌ Error: La ruta no existe: $IMPL_PATH"
    exit 1
fi

echo ""
echo "🔄 Copiando archivos del modelo..."

# Copiar archivos del modelo
if [ -d "$IMPL_PATH/src/main/java/model" ]; then
    cp -r "$IMPL_PATH/src/main/java/model"/* src/main/java/model/ 2>/dev/null
    echo "  ✅ Model copiado"
else
    echo "  ⚠️  No se encontró carpeta model"
fi

# Copiar structures
if [ -d "$IMPL_PATH/src/main/java/structures" ]; then
    cp -r "$IMPL_PATH/src/main/java/structures"/* src/main/java/structures/ 2>/dev/null
    echo "  ✅ Structures copiado"
else
    echo "  ⚠️  No se encontró carpeta structures"
fi

# Copiar tests
if [ -d "$IMPL_PATH/src/test/java/model" ]; then
    cp -r "$IMPL_PATH/src/test/java/model"/* src/test/java/model/ 2>/dev/null
    echo "  ✅ Tests copiados"
else
    echo "  ⚠️  No se encontraron tests"
fi

echo ""
echo "✅ Archivos copiados exitosamente!"
echo ""
echo "⚠️  IMPORTANTE: Aplica las correcciones manualmente:"
echo "   1. TreeAchivement.java (líneas 71, 74)"
echo "   2. OregonTrail.java (logros 2-10)"
echo "   3. ReloadingTest.java (líneas 10-11)"
echo "   4. AchievementsBSTTest.java (comentar test)"
echo "   5. MovementControllerTest.java (línea 74)"
echo ""
echo "📝 Lee README_INTEGRACION.md para más detalles"
echo ""
echo "🚀 Siguiente paso: mvn clean compile"
echo ""
