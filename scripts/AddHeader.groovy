
def copyRightLine = "Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved."

def copyrightHeader = """/*
 * ${copyRightLine}
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 """

def lineSeparator = System.getProperty("line.separator")

def scriptDirectory = new File(new File(getClass().protectionDomain.codeSource.location.path).parent)

new FileNameByRegexFinder().getFileNames(scriptDirectory.parentFile.absolutePath, /.*\.java/)
        .collect { new File(it) }
        .findAll { !it.text.contains(copyRightLine) }
        .each {

    def newText = copyrightHeader + lineSeparator + it.text
    it.text = newText
}

