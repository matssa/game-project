<?xml version="1.0" encoding="UTF-8"?>
<tileset name="fenceTiles" tilewidth="256" tileheight="256" tilecount="5" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0">
  <image width="256" height="256" source="fence/fenceLargeTurn-0-0.png"/>
 </tile>
 <tile id="1">
  <image width="256" height="256" source="fence/fenceLargeTurn-0-1.png"/>
 </tile>
 <tile id="2">
  <image width="256" height="256" source="fence/fenceLargeTurn-1-0.png"/>
 </tile>
 <tile id="3">
  <image width="256" height="256" source="fence/fenceSmallTurn.png"/>
  <objectgroup draworder="index">
   <object id="2" x="0" y="256">
    <polyline points="9,-1 12,-45"/>
   </object>
   <object id="3" x="12" y="212">
    <polyline points="0,0 9,-34 23,-67 44,-102 71,-133 95,-154 116,-168 145,-183 172,-193 209,-201 244,-203"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="4">
  <image width="256" height="128" source="fence/fenceStraight.png"/>
  <objectgroup draworder="index">
   <object id="1" x="1" y="55">
    <polyline points="0,0 255,-1"/>
   </object>
  </objectgroup>
 </tile>
</tileset>
