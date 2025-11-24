#  Sistema Bancario - FINAL POO

Sistema bancario implementado con principios de Programación Orientada a Objetos.

##  Descripción

-  **Autenticación segura** con PIN de 4 dígitos
-  **Registro de nuevos usuarios** con validación completa
-  **Gestión de cuentas** (Ahorro, Crédito e Inversión) en 3 monedas
-  **Operaciones bancarias** con validaciones robustas
-  **Conversión automática** de moneda entre cuentas propias
-  **Transferencias a terceros** por alias
-  **Historial de transacciones** detallado
-  **Resumen de patrimonio** (activos, deudas y neto) en ARS
-  **Sistema de inversiones** con simulación de mercado y rendimientos diarios

##  Monedas Soportadas

- **ARS**
- **USD**
- **EUR**

##  Tipos de Cuenta

### Cuenta de Ahorro
- No permite saldo negativo
- Disponible en cualquier moneda

### Cuenta de Crédito
- Permite sobregiro hasta el límite de crédito configurado
- El saldo puede ser negativo (deuda)

### Cuenta de Inversión
- Genera rendimientos diarios según tasas de mercado simuladas
- Tasas normales: -2% a +3% diario (95% probabilidad)
- Eventos extremos: -8% a +12% diario (5% probabilidad)
- Mantiene historial completo de fluctuaciones

##  Funcionalidades

###  Menú de Login

1. **Iniciar Sesión** - Autenticación con alias y PIN
2. **Registrarse** - Crear nuevo usuario con validación completa
3. **Ver Clientes Disponibles** - Lista de usuarios demo
4. **Salir** - Cerrar aplicación

###  Menú Principal (Usuario Autenticado)

1. **Ver Mis Cuentas** - Lista todas las cuentas con saldos
2. **Crear Nueva Cuenta** - Crear cuenta de Ahorro, Crédito o Inversión
3. **Depositar** - Agregar fondos a una cuenta
4. **Retirar** - Extraer fondos
5. **Transferir** - Transferir entre cuentas propias o a terceros
6. **Ver Historial** - Consultar transacciones de una cuenta
7. **Inversiones** - Gestionar inversiones
8. **Resumen Total** - Patrimonio neto consolidado en ARS
9. **Cerrar Sesión** - Volver al menú de login

##  Usuarios Demo

| Alias | PIN |
|-------|-----|
| juan | 1234 |
| maria | 5678 |
| carlos | 9999 |

**Autor:** TSATSORIN LEV, ACT2AP