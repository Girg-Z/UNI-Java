# <center> Progetto programmazione ad oggetti - TICKETMASTER </center>


## Lista contenuti



- [Introduzione](#introduzione)
- [Installazione](#installazione)
- [Configurazione](#configurazione)
- [Diagrammi UML](#diagrammi-uml)
- [Rotte API](#rotte-api)
    * [Metadata](#metadata)
    * [Stats](#stats)
    * [Events](#events)
      * [Operatori Logici](#operatori-logici)
- [Autori](#autori)
- [Javadoc](#Javadoc)


<br>

## Introduzione 




<center>La nostra applicazione ci permette di studiare gli eventi che avranno luogo in Europa, utilizzando le API del sito ticketmaster. <br>
L'applicazione deve permettere all'utente finale di visualizzare delle statistiche per ogni stato. Inoltre, l'utente deve poter avere la possibilità di calcolare/filtrare le statistiche in base a dei filtri scelti dall'utente stesso.
</center>
<br><br>

## Installazione
TicketMaster è installabile dal Prompt dei Comandi digitando:  
```
git clone https://github.com/Girg-Z/UNI-Java.git
```
<br>

## Configurazione
Per accedere al nostro servizio è necessario modificare la variabile ```api_key``` in [aplication.properties](https://github.com/Girg-Z/UNI-Java/blob/main/src/main/resources/application.properties).
Si può ottenere una API key gratuitamente accedendo alla pagina di [TicketMaster](https://developer.ticketmaster.com/).
Infine basterà avviare il web-server.

<br>

## Diagrammi UML

*Class Diagram*

![alt text](https://raw.githubusercontent.com/Girg-Z/UNI-Java/main/img/NewModel%20Class%20Diagram.jpg)

*Use Case Diagram*

![alt text](https://raw.githubusercontent.com/Girg-Z/UNI-Java/main/img/NewModel%20Use%20Case%20Diagram.jpg)

*Sequance Diagram*

- EVENTS

![alt text](https://raw.githubusercontent.com/Girg-Z/UNI-Java/main/img/events_sequenze.png)

- STATS

![alt text](https://raw.githubusercontent.com/Girg-Z/UNI-Java/main/img/stats_sequenze.png)


## Rotte API

 Rotta         |    Metodo    |        Funzione                        |
|---------------|--------------|----------------------------------------|
| /metadata    | GET         |  Mostra le catatteristiche degli attributi della classe Event |
| /stats        | GET         | Mostra il numero totale di eventi, numero di eventi raggruppati per genere, numero minimo/massimo/medio di eventi mensili. Filtraggio in base a: uno o più stati, uno o più generi              |
| /events         | GET          | Mostra lista eventi. Filtrabile.         |


 ### METADATA
Metadata non vuole nulla in input.  
Esempio di output:
``` Json

[
  {
    "Type": "String",
    "Sourcefield": " codice identificazione ",
    "Alias": "id"
  },
  {
    "Type": "String",
    "Sourcefield": " nome evento ",
    "Alias": "name"
  },
  {
    "Type": "String",
    "Sourcefield": " tipo evento ",
    "Alias": "type"
  },
  {
    "Type": "LocalDate",
    "Sourcefield": " data inizio ",
    "Alias": "startDate"
  },
  {
    "Type": "LocalDate",
    "Sourcefield": " data fine ",
    "Alias": "endDate"
  },
  {
    "Type": "String",
    "Sourcefield": " segmento ",
    "Alias": "segment"
  },
  {
    "Type": "String",
    "Sourcefield": " genere evento ",
    "Alias": "kind"
  },
  {
    "Type": "String",
    "Sourcefield": " Stato ",
    "Alias": "country"
  },
  {
    "Type": "String[]",
    "Sourcefield": "",
    "Alias": "COMPARABLE_FIELDS"
  }
]

```
### STATS
Come input prende il parametro filter contenente un json come da esempio:
``` Json
{"countries": ["uk", "it"],"period": 7}
```
Esempio di output:
``` Json
[
  {
    "country": "DE",
    "average": 9,
    "maximumOfEvent": 39,
    "eventsByGenre": {
      "Motorsports/Racing": 1,
      "Miscellaneous": 1,
      "Alternative": 2,
      "Rock": 150,
      "Volleyball": 2,
      "Theatre": 5,
      "Family": 1,
      "Classical": 1,
      "Dance/Electronic": 3,
      "Spectacular": 4,
      "Wrestling": 3,
      "Hip-Hop/Rap": 27
    },
    "minimumOfEvent": 0,
    "numberOfEvents": 200
  },
  {
    "country": "IT",
    "average": 0,
    "maximumOfEvent": 0,
    "eventsByGenre": {},
    "minimumOfEvent": 0,
    "numberOfEvents": 0
  }
]

```
### EVENTS
Come input prende il parametro filter contenente un json come da esempio:

``` Json
{"$and": [{"country": "DE"}, {"startDate": {"$bt": [2021-05-29, 2021-06-29]}}]}
```
##### OPERATORI LOGICI

Gli operatori logici disponibili sono i seguenti:
| Operatore    |    Funzione    |      Formato                    |
|---------------|---------------------------|----------------------------------------|
| $and     | and logico        |{"$and": [filter1, filter2]} |
| $or      | or logico         |{"$or": [filter1, filter2]}                 |
| $not      | negazione               |"field" : {"$not" : value}}    |
| $in  | trova qualsiasi valore contenuto nella lista           | {"field" : {"$in" : [value1, value2, ...]}}       |
| $nin  | negazione del filtro $in          | {"field" : {"$nin" : [value1, value2, ...]}}   |
| $gt    | >  | {"field": {"$gt": value}}   |
| $gte    | >=  | {"field": {"$gte": value}}   |
| $lt    | < | {"field": {"$lt": value}}   |
| $lte    | <=  | {"field": {"$lte": value}}   |
| $bt    | contenuto nel range | {"field": {"$bt": [value1, value2]}}   |

Esempio di output:
``` Json
{
  "totalEvents": 2,
  "events": [
    {
      "country": "DE",
      "Type": "event",
      "endDate": "2021-07-06",
      "kind": "Rock",
      "segment": "Music",
      "name": "Sting",
      "id": "Z698xZC2Z17uOFo",
      "startDate": "2021-07-06"
    },
    {
      "country": "DE",
      "Type": "event",
      "endDate": "2021-07-10",
      "kind": "Rock",
      "segment": "Music",
      "name": "Sting",
      "id": "Z698xZC2Z17uOat",
      "startDate": "2021-07-10"
    }
  ]
}
```
## Javadoc
Javadoc disponibile al seguente link:
https://girg-z.github.io/UNI-Java/


## Autori
Progetto sviluppato in modo equo da:
- Diego Manzo
- Giorgio Zanchetti