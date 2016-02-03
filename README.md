# Gnip rule parser
This is a Gnip rule parser using Scala parser combinators.

A Gnip rule has the following restrictions

1. Stop words are not allowed as stand-alone terms in queries. If you need to find a phrase that contains a stop word, either pair it with an additional term, or use the exact match operators such as “on the roof”. As long as there is at least one required and allowed term in the rule, it will be allowed. Please note that this list of stop words is subject to change, but the current stop words we use are: "a", "an", "and", "at", "but", "by", "com", "from", "http", "https", "if", "in", "is", "it", "its", "me", "my", "or", "rt", "the", "this", "to", "too", "via", "we", "www", "you"

2. Rules cannot consist of only negated terms/operators. For example, ‘-cat -dog’ is not valid.

3. Realtime and Historical PowerTrack (as well as Replay) support two forms of rules:
 * ‘Standard’ rules:
     * The entire string for a rule may be no more than 1024 characters, including all operators and spaces, with no single term exceeding 128 characters.
     * Rules may contain no more than 30 positive operators (things you want to match or filter on). If you exceed this limit when trying to create a rule, you will receive a 422 error, with a message indicating that you have exceeded one of the clause limits.
     * Rules may contain no more than 50 negative clauses. If you exceed this limit when trying to create a rule, you will receive a 422 error, with a message indicating that you have exceeded one of the clause limits.
 * ‘Long’ rules can be up to 2,048 characters long, with no single term exceeding 128 characters, and with no limits on the number of positive and negative clauses.

4. Negated ORs are not supported. Such as: apple OR -lang:en

5. Geo rules with a radius greater than 25mi are not supported. Geo rules with a bounding box comprised of any edge greater than 25mi are not supported.

6. A rule keyword or input can start with either a digit (0-9) or any non-punctuation character. Current punctuation characters are defined as the ASCII characters below. Any keyword or input that needs to start with or contains punctuation must be “quoted”. A keyword can not have a colon or parentheses unless you quote it.
```
! % & \ ' ( ) * + - . / ; < = > ? \\ , : # @ \t \r \n " [] _
and the Unicode ranges:
U+007B -- U+00BF
U+02B0 -- U+037F
U+2000 -- U+2BFF
U+FF00 -- U+FF03
U+FF05 -- U+FF0F
```
