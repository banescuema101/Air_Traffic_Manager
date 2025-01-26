Descrierea functionalitatilor din fiecare clasa, cu accent deosebit pe motivarea alegerii tipurilor
de colectii folosite in fiecare situatie: (a se vedea comentariile aferente fiecarei metode
pentru mai multe detalii despre metodele create!!)

## Clasa Airplane
Aceasta este clasa in care modelez avioanele, cu atributele specificate in enunt, statusul initializat pe baza
locatiei de destinatie, respectiv plecare. ( Daca Bucuresti este destinatie, inseamna ca avionul are status WAITING_FOR_LANDING,
in sens contrar, WAITING_FOR_LANDING).

## Clasa Runway:
-> Modelata prin genericitate, <T extends Airplane>, peste tot unde m-as putea referi
la unul dintre avioanele care extinde clasa de baza Airplane, WideBody si NarrowBody, utilizez parametrul T.

-> Aceasta este clasa in care modelez o pista de avioane. O pista, pe langa un id specific, utilizarea
ei, un anume status (enum StatusRunway) care initial este setat pe FREE, dar el putand lua si campul "OCCUPIED",
si un camp de tip LocalTime timpPistaOcupata, cu rol de a mentine timpul pana la care asupra pistei
respective nu se mai poate face nicio manevra(fiind in timpul "mort" de 5/10 minute), mai contine si o coada de avioane
pe care am ales sa o implementez folosind `PriorityQueue coadaAvioane, parametrizata cu tipul T`. Utilizand acest tip de colectie,
mi am putut face comparatori specifici, destinati in mentinerea corecta a ordinii elementelor in coada, in functie de utilizarea
pistei (aterizare sau decolare). Ambii comparatori sunt si ei parametrizati prin genericitate cu tipul T extends Airplane,
implementand Comparator<T>, pentru ca nu stiu de la bun inceput EXACT CE TIP DE AVIOANE va contine o anume pista...
Stiu doar daca e de aterizare sau de decolare.

Avantajul principal pe care l-am vazut in aceasta abordare, a fost mentinerea destul de usoara a prioritatii, nu trebuie
mereu dupa ce adaug sa revizuiesc ordinea avioanelor din ea, ci avionul este bine introdus in coada, nestricandu-mi ordinea
elementelor deja adaugate. De altfel, PriorityQueue fiind implementat in spate folosind un Heap binar, operatiile de
stergere, remove() si adaugare, offer() se fac mult mai eficient (O(logn)) decat le-as face in cadrul unei liste clasice,
unde oricum trebuie sa caut eu manual exact elementul potrivit pentru a-l sterge, si la
fel si pentru adaugare.

Aici se afla metodele toString(), de extragre a unui avion, in sensul de schimbare a statusului avionului cu cea mai mare
prioritate, din coada acelei piste, dar care sa si aiba un status de WAITING_FOR..., in momentul in care fac o manevra,
metode de adaugare a unui avion in coada si de stergere a unui avion cu un anume id din coada.
OBS: cand iteram prin coada de prioritate vrand sa afisez toate elementele din coada, a trebuit sa imi iau o coada auxiliara,
initializata folosind constructorul care ia ca parametru coada initiala, pentru a nu o strica pe cea originala,
cand apelez metoda poll().

## Clasa GestiunePiste:
Aceasta clasa o sa o instantiez o singura data in clasa Main, intrucat aici am implementat metode la nivelul pistelor,
precum eliminarea unui zbor cu un anume id ( dintr-o pista pe care trebuie sa o gasesc cat mai eficient),
afisarea zborurilor dintr-o pista cu un anume id, dar si anumite operatii pentru taskul bonus descris la finalul acestui readme.
( eliminareZbor, mutareZbor, amanareZbor)

Pistele le-am retinut facand aceasta asociere intre un anume tip de avion si o pista:
`private Map<String, Runway<WideBodyAirplane>> wideRunwayMap`
`private Map<String, Runway<NarrowBodyAirplane>> narrowRunwayMap`
Mi-am creat cate un HashMap, facand asocierea: idPista - Pista in sine, pentru a putea gasi o pista wide/narrow
mult mai usor, doar dupa id-ul acesteia. (nu mai iterez prin toate pistele de tip wide/narrow, ci extrag
direct elementul mapat la un anume id). Initial ma gandisem ca as putea sa fac doar un hashmap cu aceasta mapare
String - Runway<? extends Airplane>, dar nu a mers, fiindca era un upper bounded wildcard, deci
nu mai puteam adauga elemente wide/narrow dupa pt ca se genera eroarea aceea de compilare. Avantajul la aceste hashMap-uri,
este ca de exemplu la runway_info, eu doar incerc sa gasesc valoarea cu cheia id_runway din primul map, si daca nu o gasesc acolo,
atunci clar trebuie sa caut tot la cheia aceea dar in al doilea map.( inseamna ca are avioane de tip Narrow). deci practic ca si
complexitate in functie de timp, tot una constanta ar fi. O(2), ci nu O(1). Adaugarea, la fel este foarte eficienta, pentru ca eu
stiu clar pe ce cheie trebuie sa adaug o anumita valoare, si la fel si pentru cautare.
Am mai creat si un hashMap pentru a afisa zborurile la comanda flights_info, fara a mai lua fiecare pista in parte,
si a-i afisa avioanele, ci atunci cand adaug un avion intr-o pista, simultan adaug zborul pe cheia idAvion,
in hashMap-ul `public Map<String, Airplane> zboruriMap`. Cu avantajul de a le afisa mai usor, apare si o mica problema:
faptul ca atunci cand elimin un avion dintr-o pista, a trebuit sa am grija sa o elimin si din hashMap-ul din clasa GestiunePiste, pentru
a nu afisa eronat, ceea ce nu exista defapt pe o pista.

## Partea de BONUS:
Pentru partea de bonus m-am gandit sa introduc comanda care poate fi data sub forma:
-> timestamp change_flight delay id_zbor id_pista timestamp
-> timestamp change_flight cancel id_zbor id_pista
-> timestamp change_flight move id_zbor id_pista id_pista_noua
Un zbor poate avea o intarziere,poate fi amanat, d.p.d.v al timpului dorit de aterizare/decolare,
poate fi chiar anulat, daca o defectiune a intervenit si nu mai poate decola de exemplu,
sau poate fi dupa parerea mea si mutat pe o alta pista, daca este compatibil din punct de vedere al dimensiunii(wide/narrow)
si al decolarii/aterizarii cu ceea ce reflecta pista cu id_pista_noua. Pentru asta am introdus cele trei metode din clasa GestiunePiste.
