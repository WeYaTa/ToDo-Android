<?php

namespace App\Http\Controllers;

use App\Board;
use App\User;
use Illuminate\Http\Request;

class BoardController extends Controller
{
    //
    function getAllBoards(){
        return Board::all();
    }

    function getBoardByID($boardID){
        return Board::where('boardID' ,$boardID)->firstOrFail();
    }

    function getBoardsByUserID($userID){
        return Board::where('userID' ,$userID)->get();
        // return User::where('userID', $userID)->boards;
    }

    function addNewBoard(Request $request){
        $newBoard = new Board;
        $newBoard->userID = $request->userID;
        $newBoard->name = $request->name;
        $newBoard->background = $request->background;
        $newBoard->save();

        return $newBoard;
    }

    function updateBoard(Request $request){
        Board::where('boardID',$request->boardID)->update(['name'=>$request->name, 'background'=>$request->background]);
        return Board::where('boardID',$request->boardID)->firstOrFail();
    }

    function deleteBoard($boardID){
        return Board::where('boardID',$boardID)->delete();
    }
}
