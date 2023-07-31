# memoq-plugin-for-edt

Плагин предназначен для автоматизации рутинных действий с файлами словарей исходного языка, которые должны быть переданы лингвистам для перевода. В настоящий момент, плагин предназначен только для перевода на румынский язык.
Плагин добавляет команду "Extract source language files from the main project" в контекстное меню навигатора EDT. Команда появляется только в контекстном меню корня проекта.
Порядок работы с плагином:
1) С помощью "1C:Language tool" выполнить команду "Translations > Generate translation strings" (выбрать исходный язык, и опцию заполнения из строк исходного языка). Эта команда сформирует файлы контекстных словарей с исходным языком внутри основного проекта.
2) Запустить команду "Extract source language files from the main project". Эта команда выполнит следующие действия:
   - создаст каталог \src_ro в том же каталоге, где \src;
   - перенесет в него файлы с расширением lstr из подкаталогов основного проекта;
   - переименует эти файлы: заменить окончание "_en" на "_ro".
   - исправит ключи внутри файлов (решит проблему с неразрывными пробелами).
