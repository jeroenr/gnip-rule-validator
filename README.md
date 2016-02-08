# Gnip rule validator [![Build Status](https://travis-ci.org/jeroenr/gnip-rule-validator.svg?branch=master)](https://travis-ci.org/jeroenr/gnip-rule-validator)
This is a Gnip rule validator using [the FastParse library](https://lihaoyi.github.io/fastparse/).

## Disclaimer
A subset of the [Gnip rule syntax](http://support.gnip.com/apis/powertrack/rules.html) is now supported:

1. Stop words are not allowed as stand-alone terms in queries. If you need to find a phrase that contains a stop word, either pair it with an additional term, or use the exact match operators such as “on the roof”. As long as there is at least one required and allowed term in the rule, it will be allowed. Please note that this list of stop words is subject to change, but the current stop words we use are: "a", "an", "and", "at", "but", "by", "com", "from", "http", "https", "if", "in", "is", "it", "its", "me", "my", "or", "rt", "the", "this", "to", "too", "via", "we", "www", "you"

2. Rules cannot consist of only negated terms/operators. For example, ‘-cat -dog’ is not valid.

3. Negated ORs are not supported. Such as: apple OR -lang:en

4. A rule keyword or input can start with either a digit (0-9) or any non-punctuation character. Current punctuation characters are defined as the ASCII characters below. Any keyword or input that needs to start with or contains punctuation must be “quoted”. A keyword can not have a colon or parentheses unless you quote it.

I chose to support only generic (available across all sources) operators. If you want to support source specific operators, just add them to the src/main/resources/operators file.

```
! % & \ ' ( ) * + - . / ; < = > ? \\ , : # @ \t \r \n " [] _
and the Unicode ranges:
U+007B -- U+00BF
U+02B0 -- U+037F
U+2000 -- U+2BFF
U+FF00 -- U+FF03
U+FF05 -- U+FF0F
```

## Contributing

Pull requests are always welcome

Not sure if that typo is worth a pull request? Found a bug and know how to fix it? Do it! We will appreciate it. Any significant improvement should be documented as a [GitHub issue](https://github.com/jeroenr/gnip-rule-validator/issues) before anybody starts working on it.

I'm always thrilled to receive pull requests and will try to process them quickly. If your pull request is not accepted on the first try, don't get discouraged!
