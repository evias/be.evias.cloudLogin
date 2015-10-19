package be.evias.cloudLogin.models;

/**
 * LICENSE
 *
 Copyright 2015 Grégory Saive (greg@evias.be)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 *
**/

import be.evias.cloudLogin.models.User;

public class ArrayOfUser
    extends SimpleResult
{
    private User[] results;

    public void setResults(User[] results)
    {
        this.results = results;
    }

    public User[] getResults()
    {
        return results;
    }
}