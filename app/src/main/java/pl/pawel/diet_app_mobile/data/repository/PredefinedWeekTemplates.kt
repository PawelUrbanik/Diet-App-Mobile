package pl.pawel.diet_app_mobile.data.repository

internal data class PredefinedSlotConfig(
    val dayOffset: Int,
    val mealType: String,
    val mealName: String,
    val servings: Double = 1.0,
)

internal data class PredefinedTemplateConfig(
    val id: String,
    val name: String,
    val rawSlots: List<PredefinedSlotConfig>,
)

internal object PredefinedWeekTemplates {
    private const val MON = 0
    private const val TUE = 1
    private const val WED = 2
    private const val THU = 3
    private const val FRI = 4
    private const val SAT = 5
    private const val SUN = 6

    private const val BREAKFAST = "Śniadanie"
    private const val SECOND_BREAKFAST = "Drugie śniadanie"
    private const val LUNCH = "Obiad"
    private const val SNACK = "Podwieczorek"
    private const val DINNER = "Kolacja"

    val ALL: List<PredefinedTemplateConfig> = listOf(
        PredefinedTemplateConfig(
            id = "predef:kaska-miesiac-1",
            name = "Dieta KK - Miesiąc 1",
            rawSlots = listOf(
                // Poniedziałek
                PredefinedSlotConfig(MON, BREAKFAST, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (BANAN I ORZECHY)"),
                PredefinedSlotConfig(MON, SECOND_BREAKFAST, "KANAPKI Z POLĘDWICĄ SOPOCKĄ"),
                PredefinedSlotConfig(MON, LUNCH, "KOKTAJL BANAN, KAKAO, MLEKO"),
                PredefinedSlotConfig(MON, LUNCH, "MAKARON Z PESTO I KURCZAKIEM"),
                PredefinedSlotConfig(MON, SNACK, "SEREK WIEJSKI LIGHT Z WARZYWAMI I PIECZYWEM"),
                PredefinedSlotConfig(MON, DINNER, "KANAPKI Z TWAROŻKIEM SZCZYPIOREK I RZODKIEWKA"),
                // Wtorek
                PredefinedSlotConfig(TUE, BREAKFAST, "JAJECZNICA Z POMIDORAMI I CHLEBEM ŻYTNIM"),
                PredefinedSlotConfig(TUE, SECOND_BREAKFAST, "SEREK WIEJSKI Z POMIDOREM I CHLEBEM ŻYTNIM"),
                PredefinedSlotConfig(TUE, LUNCH, "SPAGHETTI Z MIĘSEM MIELONYM Z SZYNKI"),
                PredefinedSlotConfig(TUE, SNACK, "TWARÓG Z BANANEM"),
                PredefinedSlotConfig(TUE, DINNER, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (BANAN)"),
                // Środa
                PredefinedSlotConfig(WED, BREAKFAST, "KASZA JAGLANA Z BANANEM, KAKAO I ŻURAWINĄ"),
                PredefinedSlotConfig(WED, SECOND_BREAKFAST, "TORTILLA Z SZYNKĄ, SEREM ŻÓŁTYM I SAŁATĄ LODOWĄ"),
                PredefinedSlotConfig(WED, LUNCH, "POLĘDWICZKI WIEPRZOWE W SOSIE KOPERKOWYM"),
                PredefinedSlotConfig(WED, SNACK, "SKYR Z JABŁKIEM"),
                PredefinedSlotConfig(WED, DINNER, "SYRNIKI Z DŻEMEM TRUSKAWKOWYM"),
                // Czwartek
                PredefinedSlotConfig(THU, BREAKFAST, "TOSTY Z MOZZARELLĄ I SZYNKĄ Z PIERSI KURCZAKA"),
                PredefinedSlotConfig(THU, SECOND_BREAKFAST, "TORTILLA Z ŁOSOSIEM, SERKIEM I SAŁATĄ LODOWĄ", servings = 2.0),
                PredefinedSlotConfig(THU, LUNCH, "KURCZAK Z MOZZARELLĄ, BAZYLIĄ I POMIDOREM"),
                PredefinedSlotConfig(THU, SNACK, "RYŻ Z ODŻYWKĄ BIAŁKOWĄ I MROŻONYMI TRUSKAWKAMI"),
                PredefinedSlotConfig(THU, DINNER, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (MALINY)"),
                // Piątek
                PredefinedSlotConfig(FRI, BREAKFAST, "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA"),
                PredefinedSlotConfig(FRI, BREAKFAST, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(FRI, SECOND_BREAKFAST, "KOKTAJL KEFIR Z MALINAMI I BANANEM"),
                PredefinedSlotConfig(FRI, LUNCH, "ŁOSOŚ W PAPILOTACH Z KASZĄ GRYCZANĄ"),
                PredefinedSlotConfig(FRI, SNACK, "SEREK WIEJSKI Z POMIDOREM I CHLEBEM ŻYTNIM"),
                PredefinedSlotConfig(FRI, DINNER, "TWAROŻEK CHUDY Z RZODKIEWKĄ I GRZANKAMI"),
                // Sobota
                PredefinedSlotConfig(SAT, BREAKFAST, "RACUCHY JABŁKOWE Z CYNAMONEM NA MLEKU"),
                PredefinedSlotConfig(SAT, SECOND_BREAKFAST, "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA"),
                PredefinedSlotConfig(SAT, SECOND_BREAKFAST, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(SAT, LUNCH, "KASZA GRYCZANA Z KAPUSTĄ PEKIŃSKĄ I MIĘSEM INDYKA"),
                PredefinedSlotConfig(SAT, SNACK, "TOSTY Z MOZZARELLĄ, WARZYWAMI I POLĘDWICĄ"),
                PredefinedSlotConfig(SAT, DINNER, "KASZA GRYCZANA Z KAPUSTĄ PEKIŃSKĄ I MIĘSEM INDYKA"),
                // Niedziela
                PredefinedSlotConfig(SUN, BREAKFAST, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (MOCNA ŻURAWINA)"),
                PredefinedSlotConfig(SUN, SECOND_BREAKFAST, "TORTILLA Z SZYNKĄ, SEREM ŻÓŁTYM I SAŁATĄ LODOWĄ"),
                PredefinedSlotConfig(SUN, LUNCH, "SCHAB W SOSIE MIODOWO-MUSZTARDOWYM"),
                PredefinedSlotConfig(SUN, SNACK, "TOSTY"),
                PredefinedSlotConfig(SUN, DINNER, "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA"),
                PredefinedSlotConfig(SUN, DINNER, "CHLEB ŻYTNI RAZOWY"),
            ),
        ),
        PredefinedTemplateConfig(
            id = "predef:kk-miesiac-1-1",
            name = "Dieta KK - Miesiąc 1.1",
            rawSlots = listOf(
                // Poniedziałek
                PredefinedSlotConfig(MON, BREAKFAST, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (BANAN I ORZECHY)"),
                PredefinedSlotConfig(MON, SECOND_BREAKFAST, "KANAPKI Z POLĘDWICĄ SOPOCKĄ"),
                PredefinedSlotConfig(MON, LUNCH, "KOKTAJL BANAN, KAKAO, MLEKO"),
                PredefinedSlotConfig(MON, LUNCH, "MAKARON Z PESTO I KURCZAKIEM"),
                PredefinedSlotConfig(MON, SNACK, "SEREK WIEJSKI LIGHT Z WARZYWAMI I PIECZYWEM"),
                PredefinedSlotConfig(MON, DINNER, "KANAPKI Z TWAROŻKIEM SZCZYPIOREK I RZODKIEWKA"),
                // Wtorek
                PredefinedSlotConfig(TUE, BREAKFAST, "JAJECZNICA Z POMIDORAMI I CHLEBEM ŻYTNIM"),
                PredefinedSlotConfig(TUE, SECOND_BREAKFAST, "SEREK WIEJSKI Z POMIDOREM I CHLEBEM ŻYTNIM"),
                PredefinedSlotConfig(TUE, LUNCH, "SPAGHETTI Z MIĘSEM MIELONYM Z SZYNKI"),
                PredefinedSlotConfig(TUE, SNACK, "TWARÓG Z BANANEM"),
                PredefinedSlotConfig(TUE, DINNER, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (BANAN)"),
                // Środa
                PredefinedSlotConfig(WED, BREAKFAST, "KASZA JAGLANA Z BANANEM, KAKAO I ŻURAWINĄ"),
                PredefinedSlotConfig(WED, SECOND_BREAKFAST, "TORTILLA Z SZYNKĄ, SEREM ŻÓŁTYM I SAŁATĄ LODOWĄ"),
                PredefinedSlotConfig(WED, LUNCH, "SPAGHETTI Z MIĘSEM MIELONYM Z SZYNKI"),
                PredefinedSlotConfig(WED, SNACK, "SKYR Z JABŁKIEM"),
                PredefinedSlotConfig(WED, DINNER, "SYRNIKI Z DŻEMEM TRUSKAWKOWYM"),
                // Czwartek
                PredefinedSlotConfig(THU, BREAKFAST, "TOSTY Z MOZZARELLĄ I SZYNKĄ Z PIERSI KURCZAKA"),
                PredefinedSlotConfig(THU, SECOND_BREAKFAST, "TORTILLA Z ŁOSOSIEM, SERKIEM I SAŁATĄ LODOWĄ", servings = 2.0),
                PredefinedSlotConfig(THU, LUNCH, "KURCZAK Z MOZZARELLĄ, BAZYLIĄ I POMIDOREM"),
                PredefinedSlotConfig(THU, SNACK, "RYŻ Z ODŻYWKĄ BIAŁKOWĄ I MROŻONYMI TRUSKAWKAMI"),
                PredefinedSlotConfig(THU, DINNER, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (MALINY)"),
                // Piątek
                PredefinedSlotConfig(FRI, BREAKFAST, "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA"),
                PredefinedSlotConfig(FRI, BREAKFAST, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(FRI, SECOND_BREAKFAST, "KOKTAJL KEFIR Z MALINAMI I BANANEM"),
                PredefinedSlotConfig(FRI, LUNCH, "KURCZAK Z MOZZARELLĄ, BAZYLIĄ I POMIDOREM"),
                PredefinedSlotConfig(FRI, SNACK, "SEREK WIEJSKI Z POMIDOREM I CHLEBEM ŻYTNIM"),
                PredefinedSlotConfig(FRI, DINNER, "TWAROŻEK CHUDY Z RZODKIEWKĄ I GRZANKAMI"),
                // Sobota
                PredefinedSlotConfig(SAT, BREAKFAST, "RACUCHY JABŁKOWE Z CYNAMONEM NA MLEKU"),
                PredefinedSlotConfig(SAT, SECOND_BREAKFAST, "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA"),
                PredefinedSlotConfig(SAT, SECOND_BREAKFAST, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(SAT, LUNCH, "SCHAB DUSZONY ZE SZPINAKIEM W SOSIE MIODOWO-MUSZTARDOWYM"),
                PredefinedSlotConfig(SAT, SNACK, "TOSTY Z MOZZARELLĄ, WARZYWAMI I POLĘDWICĄ"),
                PredefinedSlotConfig(SAT, DINNER, "TORTILLA Z SZYNKĄ, SEREM ŻÓŁTYM I SAŁATĄ LODOWĄ"),
                // Niedziela
                PredefinedSlotConfig(SUN, BREAKFAST, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (MOCNA ŻURAWINA)"),
                PredefinedSlotConfig(SUN, SECOND_BREAKFAST, "TORTILLA Z SZYNKĄ, SEREM ŻÓŁTYM I SAŁATĄ LODOWĄ"),
                PredefinedSlotConfig(SUN, LUNCH, "SCHAB DUSZONY ZE SZPINAKIEM W SOSIE MIODOWO-MUSZTARDOWYM"),
                PredefinedSlotConfig(SUN, SNACK, "TOSTY"),
                PredefinedSlotConfig(SUN, DINNER, "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA"),
                PredefinedSlotConfig(SUN, DINNER, "CHLEB ŻYTNI RAZOWY"),
            ),
        ),
        PredefinedTemplateConfig(
            id = "predef:kk-miesiac-2",
            name = "Dieta KK - Miesiąc 2",
            rawSlots = listOf(
                // Poniedziałek
                PredefinedSlotConfig(MON, BREAKFAST, "OWSIANKA Z BANANEM I MASŁEM ORZECHOWYM"),
                PredefinedSlotConfig(MON, SECOND_BREAKFAST, "TOSTY Z MOZZARELLĄ"),
                PredefinedSlotConfig(MON, LUNCH, "MAKARON ZE SZPINAKIEM I KURCZAKIEM"),
                PredefinedSlotConfig(MON, SNACK, "SMOOTHIE Z KIWI, SZPINAKIEM I BANANEM"),
                PredefinedSlotConfig(MON, DINNER, "KANAPKI Z MASŁEM ORZECHOWYM I DŻEMEM ORAZ BANANEM"),
                // Wtorek
                PredefinedSlotConfig(TUE, BREAKFAST, "JAJECZNICA ZE SZCZYPIORKIEM I POMIDOREM"),
                PredefinedSlotConfig(TUE, SECOND_BREAKFAST, "CIASTKA OWSIANE Z JABŁKIEM"),
                PredefinedSlotConfig(TUE, LUNCH, "MAKARON ZE SZPINAKIEM I KURCZAKIEM"),
                PredefinedSlotConfig(TUE, SNACK, "GRZANKI Z AWOKADO, ŁOSOSIEM I PESTO"),
                PredefinedSlotConfig(TUE, DINNER, "SAŁATKA CESARZ"),
                PredefinedSlotConfig(TUE, DINNER, "CHLEB ŻYTNI RAZOWY"),
                // Środa
                PredefinedSlotConfig(WED, BREAKFAST, "NALEŚNIKI Z MĄKI PSZENNEJ ZWYKŁEJ Z DŻEMEM"),
                PredefinedSlotConfig(WED, SECOND_BREAKFAST, "KANAPKI Z WĘDZONYM ŁOSOSIEM I RUKOLĄ"),
                PredefinedSlotConfig(WED, LUNCH, "KURCZAK W PAPRYKOWYM SOSIE"),
                PredefinedSlotConfig(WED, SNACK, "CIASTKA OWSIANE Z JABŁKIEM"),
                PredefinedSlotConfig(WED, DINNER, "SAŁATKA CESARZ"),
                PredefinedSlotConfig(WED, DINNER, "CHLEB ŻYTNI RAZOWY"),
                // Czwartek
                PredefinedSlotConfig(THU, BREAKFAST, "KASZA JAGLANA Z BANANEM I KAKAO"),
                PredefinedSlotConfig(THU, SECOND_BREAKFAST, "CIASTKA OWSIANE Z JABŁKIEM"),
                PredefinedSlotConfig(THU, LUNCH, "KURCZAK W PAPRYKOWYM SOSIE"),
                PredefinedSlotConfig(THU, SNACK, "KANAPKI Z JAJKIEM, POMIDOREM I WĘDLINĄ"),
                PredefinedSlotConfig(THU, DINNER, "MAKRELA WĘDZONA Z POMIDOREM"),
                // Piątek
                PredefinedSlotConfig(FRI, BREAKFAST, "OWSIANKA NA MLEKU Z GRUSZKĄ"),
                PredefinedSlotConfig(FRI, SECOND_BREAKFAST, "SMOOTHIE Z KIWI, SZPINAKIEM I BANANEM"),
                PredefinedSlotConfig(FRI, LUNCH, "DORSZ W JARZYNACH Z KASZĄ BULGUR"),
                PredefinedSlotConfig(FRI, SNACK, "CIASTKA OWSIANE Z JABŁKIEM"),
                PredefinedSlotConfig(FRI, DINNER, "TUŃCZYK Z PIECZYWEM I WARZYWAMI"),
                // Sobota
                PredefinedSlotConfig(SAT, BREAKFAST, "JAJECZNICA ZE SZCZYPIORKIEM I POMIDOREM"),
                PredefinedSlotConfig(SAT, SECOND_BREAKFAST, "TORTILLA Z ŁOSOSIEM, SERKIEM I SAŁATĄ LODOWĄ"),
                PredefinedSlotConfig(SAT, LUNCH, "DORSZ W JARZYNACH Z KASZĄ BULGUR"),
                PredefinedSlotConfig(SAT, SNACK, "PUSZYSTE PIZZERINKI"),
                PredefinedSlotConfig(SAT, SNACK, "NADZIENIE DO PIZZY"),
                PredefinedSlotConfig(SAT, DINNER, "PUSZYSTE PIZZERINKI"),
                PredefinedSlotConfig(SAT, DINNER, "NADZIENIE DO PIZZY"),
                // Niedziela
                PredefinedSlotConfig(SUN, BREAKFAST, "RACUCHY JABŁKOWE Z CYNAMONEM I MIODEM"),
                PredefinedSlotConfig(SUN, SECOND_BREAKFAST, "KANAPKI Z WĘDZONYM ŁOSOSIEM I RUKOLĄ"),
                PredefinedSlotConfig(SUN, LUNCH, "NADZIENIE DO PIZZY"),
                PredefinedSlotConfig(SUN, LUNCH, "PUSZYSTE PIZZERINKI"),
                PredefinedSlotConfig(SUN, SNACK, "KANAPKI Z MASŁEM ORZECHOWYM I DŻEMEM"),
                PredefinedSlotConfig(SUN, DINNER, "PUSZYSTE PIZZERINKI"),
                PredefinedSlotConfig(SUN, DINNER, "NADZIENIE DO PIZZY"),
            ),
        ),
        PredefinedTemplateConfig(
            id = "predef:kk-miesiac-3",
            name = "Dieta KK - Miesiąc 3",
            rawSlots = listOf(
                // Poniedziałek
                PredefinedSlotConfig(MON, BREAKFAST, "PASTA Z MAKRELI I JAJKA"),
                PredefinedSlotConfig(MON, BREAKFAST, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(MON, SECOND_BREAKFAST, "KEFIR Z TRUSKAWKAMI I MIODEM"),
                PredefinedSlotConfig(MON, LUNCH, "RISOTTO CURRY Z KURCZAKIEM"),
                PredefinedSlotConfig(MON, SNACK, "NUTELLA Z AWOKADO I MIODEM"),
                PredefinedSlotConfig(MON, SNACK, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(MON, DINNER, "MAKARON Z PESTO I ORZECHAMI"),
                // Wtorek
                PredefinedSlotConfig(TUE, BREAKFAST, "NUTELLA Z AWOKADO I MIODEM"),
                PredefinedSlotConfig(TUE, BREAKFAST, "PŁATKI OWSIANE"),
                PredefinedSlotConfig(TUE, BREAKFAST, "BANAN"),
                PredefinedSlotConfig(TUE, SECOND_BREAKFAST, "PASTA Z MAKRELI I JAJKA"),
                PredefinedSlotConfig(TUE, SECOND_BREAKFAST, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(TUE, LUNCH, "RISOTTO CURRY Z KURCZAKIEM"),
                PredefinedSlotConfig(TUE, SNACK, "KANAPKA Z POLĘDWICĄ SOPOCKĄ I WARZYWAMI"),
                PredefinedSlotConfig(TUE, DINNER, "KASZA JAGLANA Z TRUSKAWKAMI"),
                // Środa
                PredefinedSlotConfig(WED, BREAKFAST, "KANAPKI Z WĘDZONYM ŁOSOSIEM"),
                PredefinedSlotConfig(WED, SECOND_BREAKFAST, "TORTILLA Z SZYNKĄ, SEREM ŻÓŁTYM I SAŁATĄ LODOWĄ"),
                PredefinedSlotConfig(WED, LUNCH, "SPAGHETTI Z MIĘSEM"),
                PredefinedSlotConfig(WED, SNACK, "NUTELLA Z AWOKADO I MIODEM"),
                PredefinedSlotConfig(WED, SNACK, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(WED, DINNER, "TORTILLA Z ŁOSOSIEM, SERKIEM I SAŁATĄ LODOWĄ"),
                // Czwartek
                PredefinedSlotConfig(THU, BREAKFAST, "OWSIANKA Z BANANEM I MASŁEM ORZECHOWYM"),
                PredefinedSlotConfig(THU, SECOND_BREAKFAST, "SKYR + PŁATKI + KAKAO + BANAN"),
                PredefinedSlotConfig(THU, LUNCH, "SPAGHETTI Z MIĘSEM"),
                PredefinedSlotConfig(THU, SNACK, "KANAPKI Z TWAROŻKIEM SZCZYPIOREK I RZODKIEWKA"),
                PredefinedSlotConfig(THU, DINNER, "PŁATKI JAGLANE NA MLEKU"),
                // Piątek
                PredefinedSlotConfig(FRI, BREAKFAST, "OWSIANKA Z MUSEM TRUSKAWKOWYM"),
                PredefinedSlotConfig(FRI, SECOND_BREAKFAST, "SEREK WIEJSKI LIGHT Z WARZYWAMI I PIECZYWEM"),
                PredefinedSlotConfig(FRI, LUNCH, "ŁOSOŚ Z POMARAŃCZAMI"),
                PredefinedSlotConfig(FRI, SNACK, "CHLEB Z MOZZARELLĄ I POMIDOREM"),
                PredefinedSlotConfig(FRI, DINNER, "TWAROŻEK Z TRUSKAWKAMI I ORZECHAMI"),
                // Sobota
                PredefinedSlotConfig(SAT, BREAKFAST, "PŁATKI JAGLANE NA MLEKU Z MASŁEM ORZECHOWYM I BANANEM"),
                PredefinedSlotConfig(SAT, SECOND_BREAKFAST, "KOKTAJL BANAN, KAKAO, MLEKO"),
                PredefinedSlotConfig(SAT, LUNCH, "ŁOSOŚ Z POMARAŃCZAMI"),
                PredefinedSlotConfig(SAT, SNACK, "SAŁATKA OWOCOWA POMARAŃCZA-KIWI"),
                PredefinedSlotConfig(SAT, DINNER, "OMLET Z PIECZARKAMI I POLĘDWICĄ"),
                // Niedziela
                PredefinedSlotConfig(SUN, BREAKFAST, "OWSIANKA Z BANANEM I MASŁEM ORZECHOWYM"),
                PredefinedSlotConfig(SUN, SECOND_BREAKFAST, "KANAPKI Z SEREM ŻÓŁTYM I POLĘDWICĄ"),
                PredefinedSlotConfig(SUN, LUNCH, "KURCZAK W SOSIE SŁODKO-KWAŚNYM Z MAKARONEM RYŻOWYM"),
                PredefinedSlotConfig(SUN, SNACK, "SKYR Z GRUSZKĄ"),
                PredefinedSlotConfig(SUN, DINNER, "KURCZAK W SOSIE SŁODKO-KWAŚNYM Z MAKARONEM RYŻOWYM"),
            ),
        ),
        PredefinedTemplateConfig(
            id = "predef:kk-miesiac-4",
            name = "Dieta KK - Miesiąc 4",
            rawSlots = listOf(
                // Poniedziałek
                PredefinedSlotConfig(MON, BREAKFAST, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (BANAN I ORZECHY)"),
                PredefinedSlotConfig(MON, SECOND_BREAKFAST, "SMOOTHIE Z KIWI, SZPINAKIEM I BANANEM"),
                PredefinedSlotConfig(MON, LUNCH, "RISOTTO CURRY Z KURCZAKIEM"),
                PredefinedSlotConfig(MON, SNACK, "SEREK WIEJSKI LIGHT Z WARZYWAMI I PIECZYWEM"),
                PredefinedSlotConfig(MON, DINNER, "KANAPKI Z TWAROŻKIEM SZCZYPIOREK I RZODKIEWKA"),
                // Wtorek
                PredefinedSlotConfig(TUE, BREAKFAST, "JAJECZNICA Z POMIDORAMI I CHLEBEM ŻYTNIM"),
                PredefinedSlotConfig(TUE, SECOND_BREAKFAST, "SEREK WIEJSKI Z POMIDOREM I CHLEBEM ŻYTNIM"),
                PredefinedSlotConfig(TUE, LUNCH, "SPAGHETTI Z MIĘSEM MIELONYM Z SZYNKI"),
                PredefinedSlotConfig(TUE, SNACK, "CIASTKA OWSIANE Z JABŁKIEM"),
                PredefinedSlotConfig(TUE, DINNER, "RISOTTO CURRY Z KURCZAKIEM"),
                // Środa
                PredefinedSlotConfig(WED, BREAKFAST, "GRZANKI Z AWOKADO, ŁOSOSIEM I PESTO"),
                PredefinedSlotConfig(WED, SECOND_BREAKFAST, "CIASTKA OWSIANE Z JABŁKIEM"),
                PredefinedSlotConfig(WED, LUNCH, "SPAGHETTI Z MIĘSEM MIELONYM Z SZYNKI"),
                PredefinedSlotConfig(WED, SNACK, "SKYR Z TRUSKAWKAMI"),
                PredefinedSlotConfig(WED, DINNER, "SYRNIKI Z DŻEMEM TRUSKAWKOWYM"),
                // Czwartek
                PredefinedSlotConfig(THU, BREAKFAST, "TORTILLA Z ŁOSOSIEM, SERKIEM I SAŁATĄ LODOWĄ", servings = 2.0),
                PredefinedSlotConfig(THU, SECOND_BREAKFAST, "CIASTKA OWSIANE Z JABŁKIEM"),
                PredefinedSlotConfig(THU, LUNCH, "KURCZAK W SOSIE SŁODKO-KWAŚNYM Z MAKARONEM RYŻOWYM"),
                PredefinedSlotConfig(THU, SNACK, "RYŻ Z ODŻYWKĄ BIAŁKOWĄ I MROŻONYMI TRUSKAWKAMI"),
                PredefinedSlotConfig(THU, DINNER, "KANAPKI Z MASŁEM ORZECHOWYM I DŻEMEM ORAZ BANANEM"),
                // Piątek
                PredefinedSlotConfig(FRI, BREAKFAST, "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA"),
                PredefinedSlotConfig(FRI, BREAKFAST, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(FRI, SECOND_BREAKFAST, "KOKTAJL TRUSKAWKOWO-BANANOWY"),
                PredefinedSlotConfig(FRI, LUNCH, "KURCZAK W SOSIE SŁODKO-KWAŚNYM Z MAKARONEM RYŻOWYM"),
                PredefinedSlotConfig(FRI, SNACK, "CIASTKA OWSIANE Z JABŁKIEM"),
                PredefinedSlotConfig(FRI, DINNER, "SEREK WIEJSKI Z POMIDOREM I CHLEBEM ŻYTNIM"),
                // Sobota
                PredefinedSlotConfig(SAT, BREAKFAST, "RACUCHY JABŁKOWE Z CYNAMONEM NA MLEKU"),
                PredefinedSlotConfig(SAT, SECOND_BREAKFAST, "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA"),
                PredefinedSlotConfig(SAT, SECOND_BREAKFAST, "CHLEB ŻYTNI RAZOWY"),
                PredefinedSlotConfig(SAT, LUNCH, "ŁOSOŚ Z BATATAMI"),
                PredefinedSlotConfig(SAT, LUNCH, "OGÓRKI KONSERWOWE"),
                PredefinedSlotConfig(SAT, SNACK, "TOSTY Z MOZZARELLĄ, WARZYWAMI I POLĘDWICĄ"),
                PredefinedSlotConfig(SAT, DINNER, "TORTILLA Z SZYNKĄ, SEREM ŻÓŁTYM I SAŁATĄ LODOWĄ"),
                // Niedziela
                PredefinedSlotConfig(SUN, BREAKFAST, "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (MOCNA ŻURAWINA)"),
                PredefinedSlotConfig(SUN, SECOND_BREAKFAST, "KOKTAJL TRUSKAWKOWO-BANANOWY"),
                PredefinedSlotConfig(SUN, LUNCH, "ŁOSOŚ Z BATATAMI"),
                PredefinedSlotConfig(SUN, LUNCH, "OGÓRKI KONSERWOWE"),
                PredefinedSlotConfig(SUN, SNACK, "TOSTY"),
                PredefinedSlotConfig(SUN, DINNER, "PASTA Z MAKRELI, JAJKA I KWASZONEGO OGÓRKA"),
                PredefinedSlotConfig(SUN, DINNER, "CHLEB ŻYTNI RAZOWY"),
            ),
        ),
    )
}
