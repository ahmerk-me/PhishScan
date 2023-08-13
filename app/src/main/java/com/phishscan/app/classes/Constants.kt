package com.phishscan.app.classes


const val SHARE_URL_SHORTEN = "https://unshorten.me/json"

const val BASE_URL_SHORTEN = "$SHARE_URL_SHORTEN/"

const val SHARE_URL_DOMAIN_AGE = "https://ipty.de/domage"

const val BASE_URL_DOMAIN_AGE = "$SHARE_URL_DOMAIN_AGE/"

var isEnglish = true

const val English_Regular_Font = "fonts/SFPRODISPLAYREGULAR.OTF"

const val Arabic_Regular_Font = "fonts/Cairo-Regular.ttf"

const val English_Bold_Font = "fonts/SFPRODISPLAYBOLD.OTF"

const val Arabic_Bold_Font = "fonts/Cairo-Bold.ttf"

const val NotPhishing = 1
const val MaybePhishing = 2
const val Phishing = 3

const val DomainAge = 1
const val ShortenUrl = 2