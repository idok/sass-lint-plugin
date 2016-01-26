# SassLint Plugin #

Idea support for [SassLint](https://github.com/sasstools/sass-lint) A Node-only Sass linter for both sass and scss syntax. see more [here](https://github.com/sasstools/sass-lint).<br/>
SassLint plugin for WebStorm, PHPStorm and other Idea family IDE with Javascript plugin, provides integration with SassLint and shows errors and warnings inside the editor.
* Support displaying SassLint warnings as intellij inspections
* Support for custom SassLint rules

## Getting started ##
### Prerequisites ###
* [NodeJS](http://nodejs.org/)
* IntelliJ 13.1.4 / Webstorm 8.0.4, or above.

Install sass-lint npm package [sass-lint npm](https://www.npmjs.com/package/sass-lint)</a>:<br/>
```bash
$ cd <project path>
$ npm install sass-lint
```
Or, install sass-lint globally:<br/>
```bash
$ npm install -g sass-lint
```

### Settings ###
To get started, you need to set the SassLint plugin settings:<br/>

* Go to preferences, SassLint plugin page and check the Enable plugin.
* Set the path to the nodejs interpreter bin file.
* Set the path to the sass-lint bin file. should point to ```<project path>node_modules/sass-lint/bin/sass-lint``` if you installed locally or ```/usr/local/bin/sass-lint``` if you installed globally.
  * For Windows: install sass-lint globally and point to the sass-lint cmd file like, e.g.  ```C:\Users\<username>\AppData\Roaming\npm\sass-lint.cmd```
* Select whether to let sass-lint search for ```.sass-lint.yml``` file
* You can also set a path to a custom rules directory.
