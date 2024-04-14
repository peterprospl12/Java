#!/bin/bash

# Ścieżka do pliku jar Twojej aplikacji

# Pętla od 1 do 9
for i in {1..9}
do
   # Uruchomienie aplikacji z argumentem i
   java -cp target/classes org.example.Main $i
done