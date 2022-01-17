# BBTETools
Házi begyüjtõ app ami könnyíti a mindennapi egyetemi életet.  
  
A BBTE-n nagyon sok házit kell megfelelõen elnevezni és/vagy csomagolni és kommentelni a beküldéshez.  
Ezt könnyû elhibázni, viszont néhány esetben a helytelen elnevezés árán a diák pontokat is veszíthet.  
Ez az app ezt a problémát oldja meg.  
  
## Hogyan?

Bejár egy foldert és annak minden alfolderét úgy hogy a megfelelõ fileokat keresi.(pl. Algoritmikából ".cpp" vagy ".pas" fileokat keres)  
A talált fileokat elemzi és az appben a felhasználó által megadott információ alapján átnevezi és bekommenteli.(labor szám, név, csoport szám,azonosító)  
Az ajánlott elnevezés a bemeneti fileoknak: <alpont>.<kiterjesztés> (pl. "1.cpp", "1a.cpp")  
-CSAK akkor kommentel ha nem talál már a fileban megfelelõ kommentet.  
-CSAK akkor nevezi át a file-t ha felismeri a nevében a feladatszámot.  
-Olyan feladatokat amelyek átnevezése sikertelen volt csak bemásolja és kommenteli de nem nevezi át.  

A kimeneti file vagy filerendszer tantárgyfüggõ és megfelel a kiválasztott tantárgy kiritériumainak.  
Habár a lehetséges hiba esetek számát igyekeztem minimalizálni, ajánlott a fileokat generálás után gyorsan leellenõrizni.  
  
Megjegyzés: Néhány esetben a vírusirtó megjelölheti a programot mint vírus. Aggodalomra nincs ok, ez egy "false positive".
  
  
-- Jelenleg csak algoritmikára és OOP-re mûködik az app de hamarosan ki lesz terjesztve AC-re és más olyan tantárgyakra is
  amikkel matek-infós karrierem során meg kell birkóznom --
  
## Miért?

A spórolt tíz percben több informatikát tanulhatsz =D     

## Installer készítés és más dev infók
ha van olyan tantárgy amire kibõvítenéd az appet, szeretettel fogadom a pull requesteket
https://youtu.be/XITTQGD8V1s || launch4j || inno setup compiler  

  
