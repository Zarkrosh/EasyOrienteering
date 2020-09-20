@echo off
title Despliegue en Heroku

echo [*] Estableciendo conexión con Heroku
call heroku login

echo [*] Configurando backend
call heroku git:remote -a easyorienteering-backend
call git remote remove heroku-easyo-backend
call git remote rename heroku heroku-easyo-backend

echo [*] Configurando frontend
call heroku git:remote -a easyorienteering
call git remote remove heroku-easyo-frontend
call git remote rename heroku heroku-easyo-frontend

echo [*] Desplegando backend
call git subtree push --prefix=backend heroku-easyo-backend master

echo [*] Desplegando frontend
call git subtree push --prefix=frontend heroku-easyo-frontend master