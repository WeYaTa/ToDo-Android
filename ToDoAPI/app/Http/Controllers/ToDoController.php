<?php

namespace App\Http\Controllers;

use App\Todo;
use Illuminate\Http\Request;

class ToDoController extends Controller
{
    //
    function getAllToDos(){
        return ToDo::all();
    }

    function getToDosByUserID($userID){
        return ToDo::where('userID',$userID)->get();
    }

    function getToDosByBoardID($boardID){
        return ToDo::where('boardID',$boardID)->get();
    }

    function addNewToDo(Request $request){
        $newTodo = new ToDo;
        $newTodo->userID = $request->userID;
        $newTodo->boardID = $request->boardID;
        $newTodo->status = $request->status;
        $newTodo->description = $request->description;
        $newTodo->save();

        return $newTodo;
    }

    function updateToDo(Request $request){
        ToDo::where('todoID',$request->todoID)->update(['status'=>$request->status, 'description'=>$request->description]);
        return ToDo::where('todoID',$request->todoID)->firstOrFail();
    }

    function deleteToDo($todoID){
        return ToDo::where('todoID',$todoID)->delete();
    }
}
