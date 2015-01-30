# Описание интерфейсных функицй карты

## onTerminalPositionChange(x, y, l : Number)

Перемещает маркер пользовательского терминала в заданые координаты.

`x` - координата терминала по горизонтали *
`y` - координата терминала по вертикали *
`l` - этаж, на котором находится терминал

[*] - координаты указываются в метрах

## loadMapCanvas(Canvas, [POI] : String)

Загрузка слоя картографической подложки с поэтажным планом. Может также одновременно инициализировать слой содержащий POI. `Canvas` и `POI` передаются в формате GeoJSON. 

## loadMapPOI(POI)

Загрузка слоя POI. `POI` передаются в формате GeoJSON.

## loadTechLayer(TechLayer: String)

Загрузка данных слоя содержащего служебные данные. `TechLayer` передаются в формате GeoJSON.

## setCenter(centerPoint : JSON)

Центрирование текущего центра карты на заданную точку `centerPoint`.
Формат объекта `centerPoint`:
```
{
 x: Number,
 y: Number,
 l: Number
}
```

## higlightPOI(poi_id: String, style: JSON, [unhighlight_previous]: Boolean) 
 
Подсвечивает заданный по `poi_id` POI стилем переданным в `style`. В случае если передан параметр `unhighlight_previous` происходит снятие подсветки с ранее выделенного POI.

`poi_id` - идентификатор POI 
`style` - объект описывающий стиль POI
`unhighlight_previous` - нужно ли отменить подсветку ранее подсвеченного POI 

Пример вызова функции с указанием стиля:
```
higlightPOI(2, {
    weight: 0.5,
    color: '#0072C6',
    dashArray: '15,20',
    fillColor: '#E83E59',
    fillOpacity: 0.2
}, true);
```

## loadRoutes(routes)

Загрузка данных маршрутного графа.

`routes` - содержит JSON описывающий маршрутный граф

## findRoute(startPoint: JSON, endPoint: JSON)

Выбирает кратчайший маршрут между `startPoint` и `endPoint` по графу загруженному ранее через функцию `loadRoutes`.

Формат объектов `startPoint` и `endPoint`:
```
{
    x: Number,
    y: Number,
    l: Number
}
```

## findRouteFromCurrentPosition(endPoint: JSON)

Выбирает кратчайший маршрут от текущей позиции терминала в требуемую точку `endPoint` по графу загруженному ранее через функцию `loadRoutes`.
 
Фомат объекта `endPoint`:
```
{
  x: Number,
  y: Number,
  l: Number
}
```

## higlightRoutes(Routes: JSON)

Загрузка слоя содержащего все возможные маршруты. `Routes` передаются в формате GeoJSON. Метод служит для отладки при отрисовки возможных маршрутов.

## clearPOILayer()

Очистка слоя POI. Функция вызывается без параметров.

## clearTechLayer()

Очистка технического слоя. Функция вызывается без параметров.

## clearRouteLayer()

Очистка слоя с визуализацией маршрутов. Функция вызывается без параметров.

## clearMapCanvas()

Очистка картографической подложки. Функция вызывается без параметров.



