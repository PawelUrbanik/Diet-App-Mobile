package pl.pawel.diet_app_mobile.data.local.seed

/**
 * Opisy przygotowania posiłków pochodzących z jadłospisów Katarzyny Koćwin (Miesiąc 1–4).
 * Klucz: nazwa posiłku (porównywana bez wielkości liter i białych znaków).
 */
internal object MealInstructions {
    fun forName(name: String): String? = normalized[name.trim().lowercase()]

    private val normalized: Map<String, String> by lazy {
        BY_NAME.mapKeys { (key, _) -> key.trim().lowercase() }
    }

    private val BY_NAME: Map<String, String> = mapOf(
        // ——— Miesiąc 1 / 1.1 ———
        "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (BANAN I ORZECHY)" to
            "Płatki owsiane zalej wrzątkiem do napęcznienia. Następnie dodaj jogurt naturalny, odżywkę białkową i bakalie.",
        "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (BANAN)" to
            "Płatki owsiane zalej wrzątkiem do napęcznienia. Następnie dodaj jogurt naturalny, odżywkę białkową i banana.",
        "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (MALINY)" to
            "Płatki owsiane zalej wrzątkiem do napęcznienia. Następnie dodaj jogurt naturalny, odżywkę białkową i maliny.",
        "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (MOCNA ŻURAWINA)" to
            "Płatki owsiane zalej wrzątkiem do napęcznienia. Następnie dodaj jogurt naturalny, odżywkę białkową i żurawinę.",
        "KANAPKI Z POLĘDWICĄ SOPOCKĄ" to
            "Z podanych składników przygotuj kanapki lub zrób tosty.",
        "KOKTAJL BANAN, KAKAO, MLEKO" to
            "Wszystkie składniki zblenduj.",
        "MAKARON Z PESTO I KURCZAKIEM" to
            "1. Ugotuj makaron. 2. Kurczaka pokrój w kostkę, przypraw i usmaż na patelni teflonowej, w razie potrzeby podlewając wodą. 3. Brokuł ugotuj. Całość wymieszaj na patelni z pesto bazyliowym.",
        "SEREK WIEJSKI LIGHT Z WARZYWAMI I PIECZYWEM" to
            "1. Warzywa pokrój w kostkę. 2. Wymieszaj warzywa z serkiem i przyprawami. 3. Podaj z pieczywem. 4. Posyp pestkami dyni.",
        "KANAPKI Z TWAROŻKIEM SZCZYPIOREK I RZODKIEWKA" to
            "1. Twarożek rozdrobnij widelcem i dodaj łyżkę jogurtu. 2. Wymieszaj z pokrojoną rzodkiewką i szczypiorkiem. 3. Dopraw do smaku. 4. Zjedz z chlebem.",
        "JAJECZNICA Z POMIDORAMI I CHLEBEM ŻYTNIM" to
            "1. Usmaż jajecznicę na rozgrzanym tłuszczu. 2. Pokrój pomidor. 3. Podaj jajecznicę z chlebem i pomidorem.",
        "SEREK WIEJSKI Z POMIDOREM I CHLEBEM ŻYTNIM" to
            "Warzywa pokrój, wymieszaj z serkiem i przyprawami, podaj z pieczywem.",
        "SPAGHETTI Z MIĘSEM MIELONYM Z SZYNKI" to
            "1. Makaron ugotuj. 2. Cebulę pokrój i zeszklij na oleju. 3. Dodaj mięso i podsmaż. 4. Dodaj przecier pomidorowy oraz przyprawy, w razie potrzeby podlej wodą. Podawaj z makaronem pełnoziarnistym.",
        "TWARÓG Z BANANEM" to
            "Twaróg rozdrobnij. Dodaj banana i miód.",
        "KASZA JAGLANA Z BANANEM, KAKAO I ŻURAWINĄ" to
            "Kaszę jaglaną ugotuj na mleku. Gdy będzie miękka, dodaj pokrojonego banana i kakao. Wyłóż na talerz, dodaj masło orzechowe i żurawinę.",
        "TORTILLA Z SZYNKĄ, SEREM ŻÓŁTYM I SAŁATĄ LODOWĄ" to
            "Tortillę posmaruj serkiem. Nałóż szynkę i ser żółty, a następnie roztop w mikrofali. Na koniec dodaj rozerwaną sałatę i pokrojoną paprykę. Zawiń w rulon.",
        "POLĘDWICZKI WIEPRZOWE W SOSIE KOPERKOWYM" to
            "Polędwiczki pokrój i podsmaż, zalej bulionem i duś do miękkości. Dodaj śmietanę z mąką i koperek, zagotuj. Podawaj z kaszą gryczaną i burakami.",
        "SKYR Z JABŁKIEM" to
            "Wszystkie składniki wymieszaj ze sobą.",
        "SYRNIKI Z DŻEMEM TRUSKAWKOWYM" to
            "Twaróg rozdrobnij, dodaj jajko, miód i mąkę, dokładnie wymieszaj. Na patelni rozgrzej oliwę i nakładaj porcje ciasta. Smaż z obu stron na złoty kolor. Podawaj z dżemem, posypane cynamonem.",
        "TOSTY Z MOZZARELLĄ I SZYNKĄ Z PIERSI KURCZAKA" to
            "Na chlebie ułóż pomidora, szynkę i mozzarellę. Zapiecz w opiekaczu.",
        "TORTILLA Z ŁOSOSIEM, SERKIEM I SAŁATĄ LODOWĄ" to
            "Placek tortilli posmaruj serkiem, posyp sałatą i wyłóż kawałki łososia. Całość zwiń w rulon.",
        "KURCZAK Z MOZZARELLĄ, BAZYLIĄ I POMIDOREM" to
            "1. Filet rozbij jak na kotlet. 2. W środek włóż mozzarellę i pokrojonego pomidora. 3. Zwiń i spnij wykałaczkami. 4. Obtocz w niewielkiej ilości oleju i przyprawach. 5. Upiecz w piekarniku lub uduś na patelni. Podawaj z ryżem.",
        "RYŻ Z ODŻYWKĄ BIAŁKOWĄ I MROŻONYMI TRUSKAWKAMI" to
            "Ryż ugotuj na wodzie. Podawaj z truskawkami wymieszanymi z odżywką.",
        "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA" to
            "1. Usuń ości z makreli. 2. Jajko ugotuj na twardo i pokrój w kostkę. 3. Ogórka i cebulę pokrój w drobną kostkę. 4. Wszystkie składniki wymieszaj.",
        "KOKTAJL KEFIR Z MALINAMI I BANANEM" to
            "Wszystkie składniki zblenduj.",
        "ŁOSOŚ W PAPILOTACH Z KASZĄ GRYCZANĄ" to
            "Łososia i warzywa zawiń w papilot z folii, skrop cytryną i upiecz. Podawaj z ugotowaną kaszą gryczaną.",
        "TWAROŻEK CHUDY Z RZODKIEWKĄ I GRZANKAMI" to
            "1. Twaróg przełóż do miski. 2. Warzywa pokrój w drobną kosteczkę i dodaj. 3. Dodaj jogurt i przyprawy, wymieszaj. 4. Zjedz z grzankami z chleba.",
        "RACUCHY JABŁKOWE Z CYNAMONEM NA MLEKU" to
            "Białko ubij na pianę, żółtko utrzyj z miodem. Mąkę wymieszaj z proszkiem, dodaj do żółtka z mlekiem i zmiksuj. Delikatnie wmieszaj pianę i starte jabłko. Smaż racuchy na niewielkiej ilości oleju. Podawaj z jogurtem wymieszanym z odżywką.",
        "KASZA GRYCZANA Z KAPUSTĄ PEKIŃSKĄ I MIĘSEM INDYKA" to
            "Mięso indyka podsmaż z cebulą, dodaj passatę i duś. Dorzuć poszatkowaną kapustę pekińską. Podawaj z ugotowaną kaszą gryczaną.",
        "TOSTY Z MOZZARELLĄ, WARZYWAMI I POLĘDWICĄ" to
            "1. Na chlebie ułóż pomidora, polędwicę i ser w plastrach. 2. Całość zapiecz w opiekaczu. 3. Posyp szczypiorkiem.",
        "SCHAB W SOSIE MIODOWO-MUSZTARDOWYM" to
            "Cebulę zeszklij na oleju, dodaj schab i podlej wodą. Duś pod przykryciem do miękkości. Dodaj musztardę wymieszaną z miodem, duś jeszcze chwilę. Podawaj z ziemniakami i warzywami.",
        "TOSTY" to
            "Na chlebie ułóż polędwicę, ser i ogórka. Zapiecz w opiekaczu.",
        "SCHAB DUSZONY ZE SZPINAKIEM W SOSIE MIODOWO-MUSZTARDOWYM" to
            "Schab dopraw i podsmaż z cebulą, podlej wodą i duś do miękkości. Dodaj szpinak, a następnie musztardę wymieszaną z miodem; duś jeszcze chwilę. Podawaj z kaszą gryczaną.",

        // ——— Miesiąc 2 ———
        "OWSIANKA Z BANANEM I MASŁEM ORZECHOWYM" to
            "Płatki owsiane zalej wrzątkiem. Dodaj banana i resztę składników.",
        "TOSTY Z MOZZARELLĄ" to
            "1. Na chlebie ułóż pomidora, polędwicę i ser. 2. Zapiecz w opiekaczu. 3. Posyp szczypiorkiem.",
        "MAKARON ZE SZPINAKIEM I KURCZAKIEM" to
            "Makaron ugotuj. Na patelni zeszklij czosnek, dodaj pokrojonego kurczaka i przypraw. Dodaj szpinak i duś, dopraw ziołami prowansalskimi, bazylią i miodem. Podawaj z makaronem.",
        "SMOOTHIE Z KIWI, SZPINAKIEM I BANANEM" to
            "Wszystkie składniki zmiksuj w blenderze.",
        "KANAPKI Z MASŁEM ORZECHOWYM I DŻEMEM ORAZ BANANEM" to
            "Pieczywo posmaruj masłem orzechowym, następnie delikatnie dżemem. Dodaj plasterki banana.",
        "JAJECZNICA ZE SZCZYPIORKIEM I POMIDOREM" to
            "Usmaż jajecznicę ze szczypiorkiem. Podawaj z chlebem i pomidorem.",
        "CIASTKA OWSIANE Z JABŁKIEM" to
            "Płatki owsiane wymieszaj z jajkiem. Dodaj starte jabłko, orzechy i rodzynki, wymieszaj. Uformuj ciastka na papierze do pieczenia i piecz w 160°C ok. 25–30 min.",
        "GRZANKI Z AWOKADO, ŁOSOSIEM I PESTO" to
            "1. Z chleba zrób grzanki. 2. Posmaruj je pesto, nałóż pokrojone awokado, szpinak oraz łososia.",
        "SAŁATKA CESARZ" to
            "Warzywa pokrój, ananasa pokrój w kostkę. Połowę oliwy wymieszaj z sosem sałatkowym. Na reszcie oliwy podsmaż kurczaka w przyprawie gyros. Wymieszaj wszystkie składniki.",
        "NALEŚNIKI Z MĄKI PSZENNEJ ZWYKŁEJ Z DŻEMEM" to
            "Mleko zmiksuj z mąką i jajkiem (w razie potrzeby dolej wody). Usmaż naleśniki. Podawaj z dżemem.",
        "KANAPKI Z WĘDZONYM ŁOSOSIEM I RUKOLĄ" to
            "1. Chleb posmaruj serkiem. 2. Na serek wyłóż opłukaną rukolę. 3. Połóż plasterki łososia.",
        "KURCZAK W PAPRYKOWYM SOSIE" to
            "Cebulę zeszklij na oleju, dodaj pokrojoną paprykę i duś do miękkości. Zmiksuj na sos i przelej do rondla. Dodaj pokrojonego kurczaka i duś ok. 20 min. Dodaj miód, ocet i zioła. Podawaj z ryżem.",
        "KASZA JAGLANA Z BANANEM I KAKAO" to
            "Kaszę jaglaną ugotuj na mleku do miękkości. Dodaj kakao i pokrojonego banana, podgrzej chwilę. Podawaj z orzechami włoskimi.",
        "KANAPKI Z JAJKIEM, POMIDOREM I WĘDLINĄ" to
            "1. Jajko ugotuj. 2. Chleb posmaruj masłem. 3. Nałóż polędwicę i ugotowane jajko, dodaj pomidora i ogórka.",
        "MAKRELA WĘDZONA Z POMIDOREM" to
            "Pomidora pokrój, dopraw i posyp szczypiorkiem. Makrelę zjedz z chlebem i pomidorem.",
        "OWSIANKA NA MLEKU Z GRUSZKĄ" to
            "1. Płatki owsiane ugotuj na mleku. 2. Przełóż do miski, dodaj pokrojoną gruszkę i masło orzechowe.",
        "DORSZ W JARZYNACH Z KASZĄ BULGUR" to
            "1. Rybę dopraw. 2. Jarzyny zetrzyj na grubych oczkach, dodaj olej i passatę, dopraw. 3. Plastry ryby ułóż w naczyniu żaroodpornym warstwami z warzywami. 4. Piecz pod folią ok. 40 min. 5. Ugotuj kaszę i podawaj z rybą.",
        "TUŃCZYK Z PIECZYWEM I WARZYWAMI" to
            "Zjedz tuńczyka razem z chlebem i warzywami.",
        "PUSZYSTE PIZZERINKI" to
            "Mąkę wymieszaj z drożdżami, cukrem, oliwą i solą. Dolej letniej wody i zagnieć ciasto. Odstaw do wyrośnięcia ok. 40 min. Podziel na części, uformuj placki, dodaj nadzienie i piecz ok. 20 min w 180°C.",
        "NADZIENIE DO PIZZY" to
            "Pieczarki podduś z cebulą. Mięso pokrój i dopraw. Placki posmaruj koncentratem, nałóż mięso, pieczarki oraz mozzarellę.",
        "RACUCHY JABŁKOWE Z CYNAMONEM I MIODEM" to
            "Białko ubij na pianę, żółtko utrzyj z miodem. Mąkę z proszkiem dodaj do żółtka z mlekiem, zmiksuj. Wmieszaj pianę i starte jabłko. Smaż racuchy na niewielkiej ilości oleju.",
        "KANAPKI Z MASŁEM ORZECHOWYM I DŻEMEM" to
            "Pieczywo posmaruj masłem orzechowym, następnie delikatnie dżemem. Podawaj z kiwi.",

        // ——— Miesiąc 3 ———
        "PASTA Z MAKRELI I JAJKA" to
            "1. Usuń ości z makreli. 2. Jajka ugotuj na twardo i pokrój. 3. Cebulę pokrój w drobną kostkę. 4. Wszystkie składniki wymieszaj.",
        "KEFIR Z TRUSKAWKAMI I MIODEM" to
            "Składniki zblenduj.",
        "RISOTTO CURRY Z KURCZAKIEM" to
            "1. Ryż podsmaż na patelni, dolej oliwę i wodę, duś pod przykryciem. 2. Kurczaka pokrój i usmaż osobno. 3. Warzywa pokrój i dodaj do ryżu, duś. 4. Dodaj kurczaka, dopraw solą i curry.",
        "NUTELLA Z AWOKADO I MIODEM" to
            "Wszystkie składniki zblenduj na gładką masę.",
        "MAKARON Z PESTO I ORZECHAMI" to
            "1. Ugotuj makaron. 2. Orzechy pokrój drobno. 3. Wymieszaj składniki.",
        "KANAPKA Z POLĘDWICĄ SOPOCKĄ I WARZYWAMI" to
            "Na chleb połóż sałatę, wędlinę, pomidora i ugotowane jajko.",
        "KASZA JAGLANA Z TRUSKAWKAMI" to
            "1. Kaszę ugotuj na mleku. 2. Wyłóż do miseczki, dodaj pokrojone truskawki i masło orzechowe.",
        "KANAPKI Z WĘDZONYM ŁOSOSIEM" to
            "Chleb posmaruj serkiem. Połóż sałatę lodową i pokrojoną paprykę. Na wierzchu ułóż łososia, skrop sokiem z cytryny i udekoruj koperkiem.",
        "SPAGHETTI Z MIĘSEM" to
            "1. Makaron ugotuj. 2. Cebulę zeszklij na oleju. 3. Dodaj mięso i podsmaż. 4. Dodaj przecier pomidorowy i przyprawy, w razie potrzeby podlej wodą. Podawaj z makaronem.",
        "SKYR + PŁATKI + KAKAO + BANAN" to
            "Wszystkie składniki wymieszaj ze sobą.",
        "PŁATKI JAGLANE NA MLEKU" to
            "Ugotuj płatki na mleku wg przepisu na opakowaniu (w razie potrzeby dolewaj wody). Dodaj banana i masło orzechowe.",
        "OWSIANKA Z MUSEM TRUSKAWKOWYM" to
            "1. Płatki ugotuj na mleku do kremowej konsystencji. 2. Truskawki zmiksuj z miodem i wylej na owsiankę.",
        "ŁOSOŚ Z POMARAŃCZAMI" to
            "Łososia dopraw i polej oliwą wymieszaną z miodem, odstaw do zamarynowania. Na rybie ułóż półplasterki pomarańczy. Piecz ok. 17 min w 210°C. Podawaj z ziemniakami z koperkiem.",
        "CHLEB Z MOZZARELLĄ I POMIDOREM" to
            "Mozzarellę pokrój w plasterki, połóż na chlebie i dodaj pomidora.",
        "TWAROŻEK Z TRUSKAWKAMI I ORZECHAMI" to
            "Twaróg rozdrobnij. Dodaj słodzik i truskawki, wymieszaj. Posyp orzechami i cynamonem.",
        "PŁATKI JAGLANE NA MLEKU Z MASŁEM ORZECHOWYM I BANANEM" to
            "Ugotuj płatki na mleku wg przepisu na opakowaniu. Dodaj pokrojonego banana i masło orzechowe.",
        "SAŁATKA OWOCOWA POMARAŃCZA-KIWI" to
            "Owoce pokrój w kostkę, wymieszaj z orzechami i zalej kefirem.",
        "OMLET Z PIECZARKAMI I POLĘDWICĄ" to
            "Białka oddziel od żółtek i ubij osobno. Żółtka wymieszaj z mąką i połącz z pianą. Pieczarki zrumień z cebulą. Wlej jajka na patelnię, uformuj omlet, na wierzch dodaj pieczarki i polędwicę.",
        "KANAPKI Z SEREM ŻÓŁTYM I POLĘDWICĄ" to
            "Na chleb nałóż sałatę, ser, pomidora i polędwicę.",
        "KURCZAK W SOSIE SŁODKO-KWAŚNYM Z MAKARONEM RYŻOWYM" to
            "Na oliwie podsmaż cebulę, czosnek i paprykę, dodaj pokrojonego kurczaka. Wrzuć mrożoną włoszczyznę. Wlej passatę, sok z ananasa i sos sojowy, duś pod przykryciem do miękkości. Dopraw. Makaron ugotuj i wymieszaj z zawartością patelni.",
        "SKYR Z GRUSZKĄ" to
            "Gruszkę pokrój w kostkę. Wymieszaj ze skyrem, cynamonem i orzechami.",

        // ——— Miesiąc 4 ———
        "SKYR Z TRUSKAWKAMI" to
            "Wszystkie składniki wymieszaj ze sobą.",
        "KOKTAJL TRUSKAWKOWO-BANANOWY" to
            "Wszystkie składniki zblenduj.",
        "ŁOSOŚ Z BATATAMI" to
            "Łososia przypraw solą i pieprzem, skrop sokiem z cytryny i odstaw na 30 min. Bataty pokrój na frytki, skrop oliwą i piecz w 220°C. Po 10 min dodaj łososia skórką do dołu i piecz jeszcze ok. 20 min.",
    )
}
