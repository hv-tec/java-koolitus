# Koolituse märkmed sammude kaupa

Täida koolituse käigus. Iga samm: mida koolitaja näitas → mida ise tegin → kontrollpunkt läbitud?

## 1. API key + projekt
- Koolitaja näitas: AI Studio võti; repo github.com/meelis-50315/nl-to-sql-chat
- Ise tegin: võti h@ppo.ee kontoga → `.env`; repo kloonitud `app/`, `./run.sh`
- Kontrollpunkt: ✅ 13:35 port 8080 vastab, 10 toodet seed-andmetes

## 2. Java ↔ LLM (Spring Boot ↔ Gemini)
- Koolitaja näitas: mudel gemini-3.5-flash-lite (15 RPM)
- Ise tegin: mudel keskkonnamuutujast `GEMINI_MODEL`
- Kontrollpunkt: ✅ 13:40 `/api/ask` sai Geminilt vastuse (toores SQL, sest promptid on veel tühjad)

## 3. NL → SQL
- Koolitaja näitas:
- Ise tegin:
- Kontrollpunkt:

## 4. DB → vastus
- Koolitaja näitas:
- Ise tegin:
- Kontrollpunkt:

## Koolitaja lahendus (avaldatud 15.09 15:34)
Haru `solution` repos https://github.com/meelis-50315/nl-to-sql-chat/tree/solution (commit `16dc90b`).
Erinevused meie koodist: tema `validateSqlQuery` viskab `IllegalArgumentException` → 400 „Only SELECT
queries are allowed." (selge veateade, etapp 6); meil oli `IllegalStateException` → 502 „AI Service
Unavailable" — parandatud 15.09 samamoodi. Tal puudub semikooloni-kontroll ja kommentaaride eemaldamine,
meil on. Kummalgi pole lubatud tabelite loendit. Mudel tema `application.properties`-is: `gemini-3.1-flash-lite`.
