<?php

namespace App\Http\Controllers;
use App\User;
use Illuminate\Http\Request;

class UserController extends Controller
{
    //
    function getAllUsers(){
        return User::all();
    }

    function getUserByUserID($userID){
        return User::where('userID', $userID)->firstOrFail();
    }

    function addNewUser(Request $request){
        $newUser = new User;
        $newUser->userID = $request->userID;
        $newUser->name = $request->name;
        $newUser->email = $request->email;
        $newUser->password = $request->password;
        $newUser->save();

        return $newUser;
    }

    function updateUser(Request $request){
        User::where('userID',$request->userID)->update(['name'=>$request->name, 'email'=>$request->email, 'password'=>$request->password]);
        return User::where('userID', $request->userID)->firstOrFail();
    }

    function deleteUser($userID){
        return User::where('userID',$userID)->delete();
    }

}
