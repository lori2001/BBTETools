# BBTETools
Órarend generáló és házi begyüjtő app ami könnyíti a mindennapi egyetemi életet.  

#### Megjegyzés: Néhány esetben a vírusirtó megjelölheti a programot mint vírus. Aggodalomra nincs ok, ez egy "false positive".     
(Itt a source kód... -_-)     

### Órarend generálás:
Készíts saját, nyomtatható órarendet egyetemre =)

### Házi begyüjtés:
A BBTE-n nagyon sok házit kell megfelelően elnevezni és/vagy csomagolni és kommentelni a beküldéshez.  
Ezt könnyû elhibázni, viszont néhány esetben a helytelen elnevezés árán a diák pontokat is veszíthet.  
Ez az app ezt a problémát oldja meg.  
  
## Hogyan?
### Órarend generálás:

Letölti a már megépített órarendeket az egyetem weboldaláról, majd elhelyezi egy grafikus felületre. A táblázatot editálva újrarendezheted/megváltoztathatod az óráid adatait.
Az órák egy részének van beceneve, ez egyszerûen levetíti(mappeli) az eredeti neveket egy hardcode-olt névre, de bármely név amit nem lehet mappelni megjelenik úgy ahogy =))
#### VIGYÁZZ A HELYESÍRÁSRA - ha valamit nem írsz be helyesen, nem lesz felismerve

(Azért "BETA" verzió mert csak ilyen user experiencere volt időm. "Kéne tanulni" meg ilyenek..)

### Házi begyüjtés:

Bejár egy foldert és annak minden alfolderét úgy hogy a megfelelő fileokat keresi.(pl. Algoritmikából ".cpp" vagy ".pas" fileokat keres)  
A talált fileokat elemzi és az appben a felhasználó által megadott információ alapján átnevezi és bekommenteli.(labor szám, név, csoport szám,azonosító)     
Az ajánlott elnevezés a bemeneti fileoknak: <alpont>.<kiterjesztés> (pl. "1.cpp", "1a.cpp")  
-CSAK akkor kommentel ha nem talál már a fileban megfelelő kommentet.  
-CSAK akkor nevezi át a file-t ha felismeri a nevében a feladatszámot.  
-Olyan feladatokat amelyek átnevezése sikertelen volt csak bemásolja és kommenteli de nem nevezi át.  

A kimeneti file vagy filerendszer tantárgyfüggő és megfelel a kiválasztott tantárgy kiritériumainak.  
Habár a lehetséges hiba esetek számát igyekeztem minimalizálni, ajánlott a fileokat generálás után gyorsan leellenőrizni.  
  
-- Jelenleg csak algoritmikára és OOP-re mûködik az app de hamarosan ki lesz terjesztve AC-re és más olyan tantárgyakra is
  amikkel matek-infós karrierem során meg kell birkóznom --
  
## Miért?

A spórolt húsz percben több informatikát tanulhatsz =D     

## Installer készítés és más dev infók
ha van olyan tantárgy amire kibővítenéd az appet, szeretettel fogadom a pull requesteket
https://youtu.be/XITTQGD8V1s || launch4j || inno setup compiler  

  
