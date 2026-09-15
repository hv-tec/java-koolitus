# java-koolitus

**EN:** Notes and code from a Java + LLM training (15 Sep 2026). Goal: a Spring Boot chat
assistant that answers natural-language questions about a database via Gemini (NL → SQL → answer).

**ET:** Java + LLM koolituse (15.09.2026) märkmed ja kood. Eesmärk: Spring Boot vestlusassistent,
mis vastab andmebaasi kohta esitatud küsimustele loomulikus keeles Gemini abil (NL → SQL → vastus).

## Tänud / Credits
Rakenduse alus on koolitaja **Meelis Teerni** (lektor/konsultant, BCS Koolitus, meelis.teern@bcs.ee) workshop-mall [meelis-50315/nl-to-sql-chat](https://github.com/meelis-50315/nl-to-sql-chat).
Koolitus toimus [eesti.ai](https://eesti.ai) koolituste sarjas, 15.09.2026. Aitäh!
Lisatud on hääl-sisend (Chrome Web Speech API) ja häälvastus (Jutusta.ee TTS), juhend: `docs/haalsisend/`.

The app is based on trainer **Meelis Teern's** (lecturer/consultant, BCS Koolitus, meelis.teern@bcs.ee) workshop starter [meelis-50315/nl-to-sql-chat](https://github.com/meelis-50315/nl-to-sql-chat);
the training was part of the [eesti.ai](https://eesti.ai) course series (15 Sep 2026). Voice input (Chrome Web Speech API) and voice output (Jutusta.ee TTS) added; guide in `docs/haalsisend/`.

## Struktuur / Structure
- `HANDOVER.md` — jooksev seis ja järgmine samm (loe esimesena)
- `docs/` — koolituse märkmed sammude kaupa
- `materjalid/` — slaidide ekraanipildid
- `app/` — Spring Boot rakendus (tekib koolituse 1. sammus)

## Saladused / Secrets
Gemini API võti elab ainult `.env`-is (vt `.env.example`) või keskkonnamuutujas. Mitte kunagi koodis ega gitis.
