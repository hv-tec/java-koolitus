# java-koolitus

**EN:** Notes and code from a Java + LLM training (15 Sep 2026). Goal: a Spring Boot chat
assistant that answers natural-language questions about a database via Gemini (NL → SQL → answer).

**ET:** Java + LLM koolituse (15.09.2026) märkmed ja kood. Eesmärk: Spring Boot vestlusassistent,
mis vastab andmebaasi kohta esitatud küsimustele loomulikus keeles Gemini abil (NL → SQL → vastus).

## Struktuur / Structure
- `HANDOVER.md` — jooksev seis ja järgmine samm (loe esimesena)
- `docs/` — koolituse märkmed sammude kaupa
- `materjalid/` — slaidide ekraanipildid
- `app/` — Spring Boot rakendus (tekib koolituse 1. sammus)

## Saladused / Secrets
Gemini API võti elab ainult `.env`-is (vt `.env.example`) või keskkonnamuutujas. Mitte kunagi koodis ega gitis.
