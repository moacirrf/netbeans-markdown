![Description Here](https://github.com/moacirrf/nb-java-decompiler/actions/workflows/maven-publish.yml/badge.svg)

# Heading level 1
Heading level 1(2)
===============

## Heading level 2
Heading level 2(2)
---------------
### Heading level 3
#### Heading level 4
##### Heading level 5
###### Heading level 6

Bold 1: I just love **bold text**.

Bold 2: I just love __bold text__.

Bold 3: Love**is**bold

Italic 1: Italicized text is the *cat's meow*.

Italic 2: Italicized text is the _cat's meow_.

Italic 3: A*cat*meow

Bold Italic 1: This text is ***really important***.

Bold Italic 2: This text is ___really important___.

Bold Italic 3: This text is __*really important*__.

Bold Italic 4: This text is **_really important_**.

## Blockquotes
> Dorothy followed her through many of the beautiful rooms in her castle.

# Blockquotes with Multiple Paragraphs
> Dorothy followed her through many of the beautiful rooms in her castle.
>
> The Witch bade her clean the pots and kettles and sweep the floor and keep the fire fed with wood.

# Nested Blockquotes

> Dorothy followed her through many of the beautiful rooms in her castle.
>
>> The Witch bade her clean the pots and kettles and sweep the floor and keep the fire fed with wood.

# Blockquotes with Other Elements
> #### The quarterly results look great!
>
> - Revenue was off the chart.
> - Profits were higher than ever.
>
>  *Everything* is going according to **plan**.

# Lista

# Ordered Lists
### Example 1
1. First item
2. Second item
3. Third item
4. Fourth item
### Example 2
1. First item
1. Second item
1. Third item
1. Fourth item 
### Example 3
1. First item
8. Second item
3. Third item
5. Fourth item 
### Example 4
1. First item
2. Second item
3. Third item
    1. Indented item
    2. Indented item
4. Fourth item 

# Unordered Lists
### Example 1
- First item
- Second item
- Third item
- Fourth item

### Example 2
* First item
* Second item
* Third item
* Fourth item

### Example 3
+ First item
+ Second item
+ Third item
+ Fourth item

### Example 4
- First item
- Second item
- Third item
    - Indented item
    - Indented item
- Fourth item

### Example 5
1. First item
2. Second item
3. Third item
    - Indented item
    - Indented item
4. Fourth item

# Starting Unordered List Items With Numbers
- 1968\. A great year!
- I think 1969 was second best.

# Adding Elements in Lists

To add another element in a list while preserving the continuity of the list, indent the element four spaces or one tab, as shown in the following examples.

## Paragraphs

* This is the first list item.
* Here's the second list item.

    I need to add another paragraph below the second list item.

* And here's the third list item.

## Blockquotes

* This is the first list item.
* Here's the second list item.

    > A blockquote would look great below the second list item.

* And here's the third list item.

# Code Blocks
1. Open the file.
2. Find the following code block on line 21:
        
        public class Car{
                private String model;
                private Integer year;
        }

3. Update the title to match the name of your website.

# Images
### Example 1
1. Open the file containing the Linux mascot.
2. Marvel at its beauty.

    ![Tux, the Linux mascot](https://mdg.imgix.net/assets/images/tux.png)

3. Close the file.

### Example 2
<img src="https://mdg.imgix.net/assets/images/san-juan-mountains.jpg" width="300" height="200">

### Example 3
![Tux, the Linux mascot](https://mdg.imgix.net/assets/images/tux.png)
*A nice penguin with a caption.*

### Example 4 Image with Link
 [![Tux, the Linux mascot](https://mdg.imgix.net/assets/images/tux.png "Title of image")](https://mdg.imgix.net/assets/images/tux.png)


### Example 5 SVG image

   ![Svg Image](https://github.com/moacirrf/nb-java-decompiler/actions/workflows/maven-publish.yml/badge.svg)


# Code
At the command prompt, type `nano`.

## Escaping Backticks

``Use `code` in your Markdown file.``

# Code Blocks
    <html>
      <head>
      </head>
    </html>

# Horizontal Rules (Not working, needs a custom VIEW!)

***
---
_________________

# Links
My favorite search engine is [Duck Duck Go](https://duckduckgo.com).

### Adding Titles (Must show text when hover, not working, needs a custom view)
My favorite search engine is [Duck Duck Go](https://duckduckgo.com "The best search engine for privacy").

# URLs and Email Addresses
<https://www.markdownguide.org>
<fake@example.com>

# Formatting Links
I love supporting the **[EFF](https://eff.org)**.
This is the *[Markdown Guide](https://www.markdownguide.org)*.
See the section on [`code`](#code).

# Reference-style Links

In a hole in the ground there lived a hobbit. Not a nasty, dirty, wet hole, filled with the ends
of worms and an oozy smell, nor yet a dry, bare, sandy hole with nothing in it to sit down on or to
eat: it was a [hobbit-hole][1], and that means comfort.

[1]: <https://en.wikipedia.org/wiki/Hobbit#Lifestyle> "Hobbit lifestyles"

# Tables
| Column 1 | Column 2      |  Cool |
|----------|:-------------:|------:|
| col 1 is |  left-aligned | $1600 |
| col 2 is |    centered   |   $12 |
| col 3 is | right-aligned |    $1 |

# Checkboxes

- [x] Test 1
- [x] Test 2
- [x] Test 3