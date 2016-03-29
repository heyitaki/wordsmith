# WordamentPlayer
**Note**: I do not encourage cheating whatsoever; this project was merely an intellectual exercise.

## Summary
Wordament is an online word game inspired by Boggle in which players are given 2 minutes to find as many words as they can. WordamentPlayer is a bot designed to play for the highest score possible.

WordamentPlayer moves the Wordament window into a specific location (via JNA), screenshots each tile, performs OCR (via Tesseract/Tess4J), and then solves the board using DFS and a trie-implemented dictionary. Processing and solving the board takes about 10 seconds of calculation time before starting to input words into the game board. 

## Demo
Click on the demo to see the full video.

[![WordamentPlayer Demo](https://j.gifs.com/2k2LvA.gif)](https://www.youtube.com/watch?v=qaOtIU-mFYQ)

## Next Steps
* release another demo video showing the console running alongside the WordamentPlayer gameplay
* extend usability to MacOS/Linux
* replace some hardcoded "magic" values with calcs based on user's screen size

## License
The code is released under the [MIT license](https://github.com/akshaths/WordamentPlayer/blob/master/LICENSE).
