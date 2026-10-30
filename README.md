# Spawn++

Безопасный `/spawn` для Paper/Folia: кулдаун, запрет в бою и установка точки спавна.

## Возможности

- `/spawn` — телепорт на спавн мира.
- Блокировка телепорта при активном combat tag от Combat++.
- Кулдаун и bypass-право.
- `/spawn set` — установить спавн текущего мира (admin).
- `/spawn reload` — перезагрузить конфиг.
- Работает без Combat++: тогда combat-проверка просто не срабатывает.

## Команды

| Команда | Описание |
|---|---|
| `/spawn` | Телепорт на спавн |
| `/spawn set` | Установить спавн мира |
| `/spawn reload` | Перезагрузить конфиг |

## Права

- `spawnplus.use` — использовать `/spawn` (всем).
- `spawnplus.bypass` — игнорировать кулдаун (OP).
- `spawnplus.admin` — `/spawn set` и `/spawn reload` (OP).

## Конфиг

```yaml
settings:
  spawn:
    cooldown-seconds: 10
    block-in-combat: true
```

## Версии и платформы

- Paper/Folia: `1.16.5`–`1.21.11`, отдельный jar на каждую версию.
- Spigot не поддерживается текущей Paper-сборкой.

## Сборка

```bash
./gradlew build
./gradlew build -PmcVersion=1.20.6-R0.1-SNAPSHOT
```

Jar: `build/libs/SpawnPlusPlus-1.0.0.jar`.

Лицензия: MIT.
