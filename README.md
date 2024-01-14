# RemsEngine-Android
This is a try to port Rem's Engine to Android

Since I am using AWT and LWJGL in Rem's Engine, this indirectly also is a partial port of those libraries.

## Progress
Good:
 - UI working
 - text working, basic AWT replaced

Bad:
 - sky is missing currently :/
 - some icons (emojis) are just white boxes
 - may be unstable

## Notes
There is no assimp support, because we don't need it. This is a game export,
so we only need to support the file formats that we export. We just always export custom meshes.