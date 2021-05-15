PLAGIARISM STATEMENT:
I pledge the highest level of ethical principles in support of academic excellence.
I ensure that all of my work reflects my own abilities and not those of someone else.

ANSWER TO HYPOTHETICAL QUESTION:
I would keep the max_time for calculating roots in a variable in the service, with a default value of 20 seconds.
Then I would add to the service a static function named "change_max_time_to_test_time" which can be called
from MainActivity and it would change value of max_time to 200ms.
Then I would add a bundle check in OnCreate in MainActivity to check if the bundle contained a
boolean with the key "test_time" and if it did contain it then I would call "change_max_time_to_test_time"
from the service (meaning all following calls to the service will use the new max_time).
Lastly, I would, when running tests, send a bundle to MainActivity with boolean that has the key
"test_time". (by default in actual an actual running of the app then this isn't sent)

Gromit, that's it! Cheese! We'll go somewhere where there's cheese!