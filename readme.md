# Cutcutcut
## Scelte progettuali
La codebase contiene vari package, in modo da rendere il codice più organizzato. La struttura delle classi che si occupano dello splitting e del merging è la seguente. Tutte le classi qui sotto derivano da `Action`, e le interfacce sono tra quadre.

- [FileSplitter]
    - FileSplitterByPartSize
        - FileSplitterWithEncryption
    - FileSplitterByPartCount
- [FileMerger]
    - DefaultFileMerger
        - EncryptedFileMerger

Il fatto che derivino da `Action` permette loro di essere aggiunte nell'array di `Action`s che c'è in `MainPanel`. Non ho utilizzato una classe `Queue` lì perché non mi sarebbe stato molto utile, visto che `addAction`/`removeAction`/`replaceAction` modificano anche parti della GUI. La versione CLI del programma non permette l'uso di una queue, seguendo la filosofia GNU del delegare quella funzionalità a qualche altro programma (ad esempio, usando scripting via `bash`).
