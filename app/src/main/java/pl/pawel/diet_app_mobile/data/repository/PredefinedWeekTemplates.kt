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
    )
}
